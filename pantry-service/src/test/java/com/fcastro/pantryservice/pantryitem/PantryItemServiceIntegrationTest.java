package com.fcastro.pantryservice.pantryitem;

import com.fcastro.app.model.ProductDto;
import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.event.PurchaseEventDto;
import com.fcastro.pantryservice.event.PurchaseCreateEventProducer;
import com.fcastro.pantryservice.exception.QuantityNotAvailableException;
import com.fcastro.pantryservice.pantry.PantryDto;
import com.fcastro.pantryservice.pantry.PantryService;
import com.fcastro.pantryservice.product.Product;
import com.fcastro.pantryservice.product.ProductRepository;
import com.fcastro.pantryservice.product.ProductService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE
        , properties = {"spring.security.enabled=false", "spring.kafka.enabled=false"})
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PantryItemServiceIntegrationTest {

    @Container
    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer(PostgreSQLContainer.IMAGE)
            .withDatabaseName("pantry-db_it")
            .withUsername("pantry")
            .withPassword("pantry");

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    PantryItemService pantryItemService;

    @Autowired
    PantryItemRepository pantryItemRepository;

    @MockBean
    PurchaseCreateEventProducer purchaseCreateEventProducer;

    @Autowired
    PantryService pantryService;

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    KafkaConfigData kafkaConfigData;

    PantryDto pantry;
    ProductDto product1, product2;
    Set<PantryItemDto> itemList = new HashSet<>();

    @BeforeAll
    public void setupEnv() {

        pantry = pantryService.get("PANTRY1")
                .orElseGet(() -> pantryService.save(PantryDto.builder().name("PANTRY1").isActive(true).type("A").build()));

        //Avoid calling service.save since it triggers Kafka event
        product1 = productService.get("MILK")
                .orElseGet(() -> {
                    var entity = productRepository.saveAndFlush(Product.builder().code("MILK").description("Vollmilch").size("1L").build());
                    return productService.get(entity.getId()).get();
                });
        product2 = productService.get("ICE_TEA")
                .orElseGet(() -> {
                    var entity = productRepository.save(Product.builder().code("ICE_TEA").description("Ice Tea").size("1L").build());
                    return productService.get(entity.getId()).get();
                });

        var item1 = pantryItemService.get(pantry.getId(), product1.getId())
                .orElseGet(() -> pantryItemService.save(PantryItemDto.builder()
                        .pantryId(pantry.getId())
                        .productId(product1.getId())
                        .build()));

        var item2 = pantryItemService.get(pantry.getId(), product1.getId())
                .orElseGet(() -> pantryItemService.save(PantryItemDto.builder()
                        .pantryId(pantry.getId())
                        .productId(product2.getId())
                        .build()));

        itemList.add(item1);
        itemList.add(item2);
    }

    @AfterAll
    public void cleanup() {
        //cleanup
        itemList.forEach(item -> pantryItemService.delete(item.getPantryId(), item.getProductId()));

        //Avoid calling service.delete since it triggers Kafka event
        productRepository.delete(Product.builder().id(product1.getId()).build());
        productRepository.delete(Product.builder().id(product2.getId()).build());

        if (pantry != null) pantryService.delete(pantry.getId());
    }

    @Test
    public void givenHighLevelQty_whenConsumeProduct_shouldCalculateQtyAndDoNotPurchase() {

        //given
        //PantryItem{currentQty = 10 and IdealQty = 10}, PurchaseItem{consumed = 1}
        itemList.add(pantryItemService.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(product1.getId()) //MILK_TEST
                .currentQty(10)
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build()));

        //when
        var consumedDto = PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(product1.getId()).qty(1).build();
        var item = pantryItemService.consumePantryItem(consumedDto);

        //then
        assertThat(item.getCurrentQty()).isEqualTo(9);
        assertThat(item.getProvisionedQty()).isEqualTo(0);
        assertThat(item.getLastProvisioning()).isNull();
    }

    @Test
    public void givenValidIds_whenConsumeProduct_shouldCalculateQtyAndPurchaseMore() {

        //given
        doNothing().when(purchaseCreateEventProducer).send(any(PurchaseEventDto.class));

        //PantryItem{currentQty = 9 and IdealQty = 10}, PantryItemConsumedDto{qty = 4}
        itemList.add(pantryItemService.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(product1.getId()) //MILK_TEST
                .currentQty(9)
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build()));


        //when
        var consumedDto = PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(product1.getId()).qty(4).build();
        var item = pantryItemService.consumePantryItem(consumedDto);

        //then
        assertThat(item.getCurrentQty()).isEqualTo(5);
        assertThat(item.getProvisionedQty()).isEqualTo(5);
        assertThat(item.getLastProvisioning()).isEqualToIgnoringSeconds(LocalDateTime.now());
    }

    @Test
    public void givenListOfItems_whenConsumeProduct_shouldCalculateQtyAndDoNotPurchase() {

        //given
        itemList.add(pantryItemService.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(product1.getId()) //MILK_TEST
                .currentQty(10)
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build()));

        itemList.add(pantryItemService.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(product2.getId()) //ICE_TEE_TEST
                .currentQty(10)
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build()));

        //when
        var consumedItems = new ArrayList<PantryItemConsumedDto>();
        consumedItems.add(PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(product1.getId()).qty(1).build());
        consumedItems.add(PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(product2.getId()).qty(2).build());
        var items = pantryItemService.consumePantryItem(pantry.getId(), consumedItems);

        //then
        assertThat(items).isNotNull();
        assertThat(items.size()).isEqualTo(2);
        assertThat(items.get(0).getCurrentQty()).isEqualTo(9);
        assertThat(items.get(0).getProvisionedQty()).isEqualTo(0);
        assertThat(items.get(0).getLastProvisioning()).isNull();
        assertThat(items.get(1).getCurrentQty()).isEqualTo(8);
        assertThat(items.get(1).getProvisionedQty()).isEqualTo(0);
        assertThat(items.get(1).getLastProvisioning()).isNull();
    }

    @Test
    public void givenListOfItems_whenConsumeProduct_shouldRollbackAfterException() {

        //given
        itemList.add(pantryItemService.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(product1.getId())  //MILK_TEST
                .currentQty(10)
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build()));

        itemList.add(pantryItemService.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(product2.getId())  //ICE_TEE_TEST
                .currentQty(0)  //This will throw QuantityNotAvailableException
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build()));

        //when
        var consumedItems = new ArrayList<PantryItemConsumedDto>();
        consumedItems.add(PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(product1.getId()).qty(1).build());
        consumedItems.add(PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(product2.getId()).qty(2).build());
        Assertions.assertThrows(QuantityNotAvailableException.class, () -> pantryItemService.consumePantryItem(pantry.getId(), consumedItems));

        //then check if the update to item1 had been rolled back
        var item = pantryItemService.get(pantry.getId(), product1.getId()).get();
        assertThat(item).isNotNull();
        assertThat(item.getCurrentQty()).isEqualTo(10);
    }
}

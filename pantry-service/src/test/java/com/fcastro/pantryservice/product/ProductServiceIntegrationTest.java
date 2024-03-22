package com.fcastro.pantryservice.product;

import com.fcastro.app.model.ProductDto;
import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.event.ProductEventDto;
import com.fcastro.pantryservice.event.ProductEventProducer;
import com.fcastro.pantryservice.event.PurchaseCreateEventProducer;
import com.fcastro.pantryservice.exception.DatabaseConstraintException;
import com.fcastro.pantryservice.pantry.PantryDto;
import com.fcastro.pantryservice.pantry.PantryService;
import com.fcastro.pantryservice.pantryItem.PantryItemDto;
import com.fcastro.pantryservice.pantryItem.PantryItemService;
import org.junit.jupiter.api.*;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"spring.security.enabled=false", "spring.kafka.enabled=false"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductServiceIntegrationTest {

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
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PantryItemService pantryItemService;

    @MockBean
    ProductEventProducer productEventProducer;

    @MockBean
    PurchaseCreateEventProducer purchaseCreateEventProducer;

    @Autowired
    PantryService pantryService;

    @Spy
    KafkaConfigData kafkaConfigData;

    PantryDto pantry;
    ProductDto product1;
    Set<PantryItemDto> itemList = new HashSet<>();

    @BeforeAll
    public void setupEnv() {
        pantry = pantryService.get("PANTRY1")
                .orElseGet(() -> pantryService.save(PantryDto.builder().name("PANTRY1").isActive(true).type("A").build()));

        //Avoid calling service.save since it triggers Kafka event
        product1 = productService.get("ICE_TEA")
                .orElseGet(() -> {
                    var entity = productRepository.saveAndFlush(Product.builder().code("ICE_TEA").description("Ice Tea").size("1L").build());
                    return productService.get(entity.getId()).get();
                });

        var item1 = pantryItemService.get(pantry.getId(), product1.getId())
                .orElseGet(() -> pantryItemService.save(PantryItemDto.builder()
                        .pantryId(pantry.getId())
                        .productId(product1.getId())
                        .build()));

        itemList.add(item1);
    }

    @AfterAll
    public void cleanup() {
        //cleanup
        itemList.forEach(item -> pantryItemService.delete(item.getPantryId(), item.getProductId()));

        //Avoid calling service.delete since it triggers Kafka event
        productRepository.delete(Product.builder().id(product1.getId()).build());

        if (pantry != null) pantryService.delete(pantry.getId());
    }

    @Test
    public void deleteProductWithAPantryItem_shouldReturnException() {
        doNothing().when(productEventProducer).send(any(ProductEventDto.class));
        Assertions.assertThrows(DatabaseConstraintException.class, () -> productService.delete(product1.getId()));
    }
}

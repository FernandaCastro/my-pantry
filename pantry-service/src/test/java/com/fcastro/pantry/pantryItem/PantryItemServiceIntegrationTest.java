package com.fcastro.pantry.pantryItem;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.pantry.config.EventProducer;
import com.fcastro.pantry.exception.QuantityNotAvailableException;
import com.fcastro.pantry.pantry.PantryDto;
import com.fcastro.pantry.pantry.PantryService;
import com.fcastro.pantry.product.ProductDto;
import com.fcastro.pantry.product.ProductService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PantryItemServiceIntegrationTest {

    @Autowired
    PantryItemService service;

    @Autowired
    PantryItemRepository repository;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    PantryService pantryService;

    @Autowired
    ProductService productService;

    @Autowired
    KafkaConfigData kafkaConfigData;

    PantryDto pantry = null;
    List<ProductDto> productList = new ArrayList<>();
    Set<PantryItemDto> itemList = new HashSet<>();

    @BeforeAll
    public void setupEnv() {
        pantry = pantryService.save(PantryDto.builder().name("PANTRY_TEST").isActive(true).type("A").build());

        productList.add(productService.save(ProductDto.builder().code("MILK_TEST").description("Vollmilch").size("1L").build()));
        productList.add(productService.save(ProductDto.builder().code("ICE_TEA_TEST").description("Ice Tea").size("1L").build()));
    }

    @AfterAll
    public void cleanup() {
        //cleanup
        itemList.forEach(item -> service.delete(item.getPantryId(), item.getProductId()));

        productList.forEach(item -> productService.delete(item.getId()));

        if (pantry != null) pantryService.delete(pantry.getId());
    }

    @Test
    public void givenHighLevelQty_whenConsumeProduct_shouldCalculateQtyAndDoNotPurchase() {

        //given
        //PantryItem{currentQty = 10 and IdealQty = 10}, PurchaseItem{consumed = 1}
        itemList.add(service.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(productList.get(0).getId()) //MILK_TEST
                .currentQty(10)
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build()));

        //when
        var consumedDto = PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(productList.get(0).getId()).qty(1).build();
        var item = service.consumePantryItem(consumedDto);

        //then
        assertThat(item.getCurrentQty()).isEqualTo(9);
        assertThat(item.getProvisionedQty()).isEqualTo(0);
        assertThat(item.getLastProvisioning()).isNull();
    }

    @Test
    public void givenValidIds_whenConsumeProduct_shouldCalculateQtyAndPurchaseMore() {

        //given
        //PantryItem{currentQty = 9 and IdealQty = 10}, PantryItemConsumedDto{qty = 4}
        itemList.add(service.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(productList.get(0).getId()) //MILK_TEST
                .currentQty(9)
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build()));


        //when
        var consumedDto = PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(productList.get(0).getId()).qty(4).build();
        var item = service.consumePantryItem(consumedDto);

        //then
        assertThat(item.getCurrentQty()).isEqualTo(5);
        assertThat(item.getProvisionedQty()).isEqualTo(5);
        assertThat(item.getLastProvisioning()).isEqualToIgnoringSeconds(LocalDateTime.now());
    }

    @Test
    public void givenListOfItems_whenConsumeProduct_shouldCalculateQtyAndDoNotPurchase() {

        //given
        itemList.add(service.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(productList.get(0).getId()) //MILK_TEST
                .currentQty(10)
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build()));

        itemList.add(service.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(productList.get(1).getId()) //ICE_TEE_TEST
                .currentQty(10)
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build()));

        //when
        var consumedItems = new ArrayList<PantryItemConsumedDto>();
        consumedItems.add(PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(productList.get(0).getId()).qty(1).build());
        consumedItems.add(PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(productList.get(1).getId()).qty(2).build());
        var items = service.consumePantryItem(pantry.getId(), consumedItems);

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
        itemList.add(service.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(productList.get(0).getId())  //MILK_TEST
                .currentQty(10)
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build()));

        itemList.add(service.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(productList.get(1).getId())  //ICE_TEE_TEST
                .currentQty(0)  //This will throw QuantityNotAvailableException
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build()));

        //when
        var consumedItems = new ArrayList<PantryItemConsumedDto>();
        consumedItems.add(PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(productList.get(0).getId()).qty(1).build());
        consumedItems.add(PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(productList.get(1).getId()).qty(2).build());
        Assertions.assertThrows(QuantityNotAvailableException.class, () -> service.consumePantryItem(pantry.getId(), consumedItems));

        //then check if the update to item1 had been rolled back
        var item = service.get(pantry.getId(), productList.get(0).getId()).get();
        assertThat(item).isNotNull();
        assertThat(item.getCurrentQty()).isEqualTo(10);
    }
}

package com.fcastro.pantryInventory.pantryItem;

import com.fcastro.pantryInventory.event.PurchaseItemEventProducer;
import com.fcastro.pantryInventory.pantry.PantryDto;
import com.fcastro.pantryInventory.pantry.PantryService;
import com.fcastro.pantryInventory.product.ProductDto;
import com.fcastro.pantryInventory.product.ProductService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PantryItemServiceIntegrationTest {

    @Autowired
    PantryItemService service;

    @Autowired
    PantryItemRepository repository;

    @Autowired
    PurchaseItemEventProducer eventProducer;

    @Autowired
    PantryService pantryService;

    @Autowired
    ProductService productService;

    PantryDto pantry = null;
    ProductDto product = null;
    PantryItemDto item = null;

    @BeforeAll
    public void setupEnv() {
        pantry = pantryService.save(PantryDto.builder().name("PANTRY1").isActive(true).type("A").build());
        product = productService.save(ProductDto.builder().code("MILK").description("Vollmilch").size("1L").build());
    }

    @AfterAll
    public void cleanup() {
        //cleanup
        if (item != null) {
            service.delete(pantry.getId(), product.getId());
        }
        if (product != null) {
            productService.delete(product.getId());
        }
        if (pantry != null) {
            pantryService.delete(pantry.getId());
        }
    }

    @Test
    public void givenHighLevelQty_whenConsumeProduct_shouldCalculateQtyAndDoNotPurchase() {

        //given
        //PantryItem{currentQty = 10 and IdealQty = 10}, PurchaseItem{consumed = 1}
        item = service.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(product.getId())
                .currentQty(10)
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build());
        var consumedDto = PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(product.getId()).qty(1).build();

        //when

        item = service.consumePantryItem(consumedDto);

        //then
        assertThat(item.getCurrentQty()).isEqualTo(9);
        assertThat(item.getProvisionedQty()).isEqualTo(0);
        assertThat(item.getLastProvisioning()).isNull();
    }

    @Test
    public void givenValidIds_whenConsumeProduct_shouldCalculateQtyAndPurchaseMore() {

        //given
        //PantryItem{currentQty = 9 and IdealQty = 10}, PantryItemConsumedDto{qty = 4}
        item = service.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(product.getId())
                .currentQty(9)
                .idealQty(10)
                .provisionedQty(0)
                .lastProvisioning(null)
                .build());
        var consumedDto = PantryItemConsumedDto.builder().pantryId(pantry.getId()).productId(product.getId()).qty(4).build();

        //when
        item = service.consumePantryItem(consumedDto);

        //then
        assertThat(item.getCurrentQty()).isEqualTo(5);
        assertThat(item.getProvisionedQty()).isEqualTo(5);
        assertThat(item.getLastProvisioning()).isEqualToIgnoringSeconds(LocalDateTime.now());
    }
}

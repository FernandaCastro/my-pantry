package com.fcastro.pantry.product;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.model.ProductDto;
import com.fcastro.pantry.config.PurchaseCreateEventProducer;
import com.fcastro.pantry.exception.DatabaseConstraintException;
import com.fcastro.pantry.pantry.PantryDto;
import com.fcastro.pantry.pantry.PantryService;
import com.fcastro.pantry.pantryItem.PantryItemDto;
import com.fcastro.pantry.pantryItem.PantryItemService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductServiceIntegrationTest {

    @Autowired
    ProductService service;

    @Autowired
    ProductRepository repository;

    @Autowired
    PantryItemService pantryItemService;

    @Autowired
    PurchaseCreateEventProducer eventProducer;

    @Autowired
    PantryService pantryService;

    @Autowired
    KafkaConfigData kafkaConfigData;

    PantryDto pantry = null;
    List<ProductDto> productList = new ArrayList<>();
    Set<PantryItemDto> itemList = new HashSet<>();

    @BeforeAll
    public void setupEnv() {
        pantry = pantryService.save(PantryDto.builder().name("PANTRY1").isActive(true).type("A").build());
        productList.add(service.save(ProductDto.builder().code("ICE_TEA").description("Ice Tea").size("1L").build()));
        itemList.add(pantryItemService.save(PantryItemDto.builder()
                .pantryId(pantry.getId())
                .productId(productList.get(0).getId())
                .build()));
    }

    @AfterAll
    public void cleanup() {
        //cleanup
        itemList.forEach(item -> pantryItemService.delete(item.getPantryId(), item.getProductId()));

        productList.forEach(item -> service.delete(item.getId()));

        if (pantry != null) pantryService.delete(pantry.getId());
    }

    @Test
    public void deleteProductWithAPantryItem_shouldReturnException() {
        Assertions.assertThrows(DatabaseConstraintException.class, () -> service.delete(productList.get(0).getId()));
    }
}

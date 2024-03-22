package com.fcastro.purchaseservice.config;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.event.ProductEvent;
import com.fcastro.kafka.exception.EventProcessingException;
import com.fcastro.purchaseservice.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(ProductEventListener.class);

    private final ProductService productService;

    private final KafkaConfigData kafkaConfigData;

    public ProductEventListener(ProductService productService, KafkaConfigData kafkaConfigData) {
        this.productService = productService;
        this.kafkaConfigData = kafkaConfigData;
    }

    @KafkaListener(topics = "productTopic", containerFactory = "kafkaListenerContainerFactory")
    protected void listener(ProductEvent event) {
        LOG.info("Event Received: Topic[{}], Data[{}]", kafkaConfigData.getProductTopic(), event.toString());

        try {
            if (event.getData() == null) {
                throw new EventProcessingException("ProductEvent received, but attribute data is null.");
            }
            productService.processProductEvent(event.getData());
        } catch (EventProcessingException ex) {
            //TODO: What should be done? Save to reprocess later or manually?
            LOG.error(ex.getMessage());
        }
    }
}

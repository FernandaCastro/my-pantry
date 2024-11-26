package com.fcastro.pantryservice.event;

import com.fcastro.kafka.config.KafkaProperties;
import com.fcastro.kafka.event.AccountEvent;
import com.fcastro.kafka.exception.EventProcessingException;
import com.fcastro.pantryservice.pantry.PantryService;
import com.fcastro.pantryservice.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AccountEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(AccountEventListener.class);

    private final PantryService pantryService;
    private final ProductService productService;
    private final KafkaProperties kafkaConfigData;

    public AccountEventListener(PantryService pantryService, ProductService productService, KafkaProperties kafkaConfigData) {
        this.pantryService = pantryService;
        this.productService = productService;
        this.kafkaConfigData = kafkaConfigData;
    }

    //TODO: HOW TO define <topics> getting value from configuration files?
    @KafkaListener(topics = "accountTopic", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    protected void listener(AccountEvent event, Acknowledgment acknowledgment) {

        try {

            if (event.getData() == null) {
                LOG.error("accountTopic received, but attribute data is null.");
                return;
            }

            LOG.info("Event Received: Topic[{}], Data[{}]", "accountTopic", event.getData().toString());
            pantryService.delete(event.getData());
            productService.delete(event.getData());

        } catch (EventProcessingException ex) {
            //TODO: What should be done? Save to reprocess later or manually?
            StringBuilder sb = new StringBuilder();
            ex.getThrowableMap().entrySet().forEach(
                    (entry) -> {
                        sb.append(entry.getKey().toString()).append(" - ").append(entry.getValue().toString());
                    }
            );
            LOG.error(ex.getMessage() + "List of Item and Error: {}", sb);

        } finally {
            acknowledgment.acknowledge();
        }
    }
}

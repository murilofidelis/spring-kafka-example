package br.com.kafka.example.listener;

import br.com.kafka.example.dto.SaleDTO;
import br.com.kafka.example.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SalesListener {

    private final SaleService saleService;

    @Transactional
    @KafkaListener(
            topics = "${spring.kafka.topics.sales.name}",
            groupId = "sales-group",
            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "1")
    public void salesListener(@Payload ConsumerRecord<String, SaleDTO> record, Acknowledgment ack) {
        log.info("Received Message, offset: {}, partition: {}", record.offset(), record.partition());
        log.info("key: {}", record.key());
        try {
            process(record.value());
        } finally {
            ack.acknowledge();
        }
    }

    private void process(SaleDTO sale) {
        saleService.save(sale);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.sales-dlt.name}",
            groupId = "sales-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void salesListenerListDlt(@Payload ConsumerRecord<String, SaleDTO> record) {
        log.warn("salesListenerListDlt: {}", record.value());
    }

}

package br.com.kafka.example.config;

import br.com.kafka.example.dto.SaleDTO;
import br.com.kafka.example.exception.InvalidDataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.RecoveringBatchErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Slf4j
@EnableKafka
@Configuration
public class kafkaConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Value("${spring.kafka.max-retry}")
    private Integer maxRetry;

    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(kafkaProperties.buildAdminProperties());
    }

    @Bean
    public KafkaConsumer<String, String> kafkaConsumer() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new KafkaConsumer<>(props);
    }

    @Bean
    public ConsumerFactory<String, SaleDTO> salesConsumerFactory() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(SaleDTO.class, false));
    }

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, SaleDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SaleDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 60000);
        DefaultKafkaConsumerFactory<String, SaleDTO> kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(SaleDTO.class, false));
        factory.setConsumerFactory(kafkaConsumerFactory);
        factory.setErrorHandler(seekToCurrentErrorHandler());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setSyncCommits(Boolean.TRUE);
        return factory;
    }

    @Bean(name = "kafkaListenerBatchContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, SaleDTO> batchContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SaleDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(this.salesConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.setBatchListener(true);
        factory.setBatchErrorHandler(this.retryingBatchErrorHandler());
        return factory;
    }

    @Bean
    public ProducerFactory<Object, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private RecoveringBatchErrorHandler retryingBatchErrorHandler() {
        RecoveringBatchErrorHandler handler = new RecoveringBatchErrorHandler(this.deadLetterPublishingRecoverer(), new FixedBackOff(3000L, this.maxRetry));
        handler.setLogLevel(KafkaException.Level.INFO);
        handler.addNotRetryableExceptions(InvalidDataException.class);
        return handler;
    }

    private SeekToCurrentErrorHandler seekToCurrentErrorHandler() {
        SeekToCurrentErrorHandler handler = new SeekToCurrentErrorHandler(this.deadLetterPublishingRecoverer(), new FixedBackOff(3000L, this.maxRetry));
        handler.setLogLevel(KafkaException.Level.INFO);
        handler.addNotRetryableExceptions(NullPointerException.class);
        return handler;
    }

    private DeadLetterPublishingRecoverer deadLetterPublishingRecoverer() {
        KafkaOperations<Object, Object> kafkaOperations = this.kafkaTemplate();
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(kafkaOperations, (config, ex) -> new TopicPartition(config.topic() + ".DLT", config.partition()));
        BiFunction<ConsumerRecord<?, ?>, Exception, Headers> consumerRecordExceptionHeader = this.addCustomHeadersException();
        deadLetterPublishingRecoverer.setHeadersFunction(consumerRecordExceptionHeader);
        return deadLetterPublishingRecoverer;
    }

    private BiFunction<ConsumerRecord<?, ?>, Exception, Headers> addCustomHeadersException() {
        return (config, ex) -> {
            Headers headers = config.headers();
            if (ex.getCause() != null) {
                this.addHeaders(headers, ex);
            }
            return headers;
        };
    }

    private void addHeaders(Headers headers, Exception ex) {
        String exceptionClassName = ex.getCause().getClass().getName();
        headers.add("APP_EXCEPTION", exceptionClassName.getBytes(StandardCharsets.UTF_8));
    }
}

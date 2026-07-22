package diana.dev.booking_service.kafka;


import diana.dev.shared.kafka.BookingConfirmedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    DefaultKafkaProducerFactory<Long, BookingConfirmedEvent> bookingConfirmedEventDefaultKafkaProducerFactory(
        KafkaProperties kafkaProperties
    ) {
        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(producerProperties);
    }


    @Bean
    KafkaTemplate<Long, BookingConfirmedEvent> bookingConfirmedEventKafkaTemplate (
        DefaultKafkaProducerFactory<Long, BookingConfirmedEvent> bookingConfirmedEventDefaultKafkaProducerFactory
    ) {
        return new KafkaTemplate<>(bookingConfirmedEventDefaultKafkaProducerFactory);
    }

}

package com.analytics.engine.config;

import com.analytics.engine.dto.WebEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.sender.SenderOptions;

@Configuration
@Profile("!local")
public class KafkaProducerConfig {

    @Bean
    public ReactiveKafkaProducerTemplate<String, WebEvent> reactiveKafkaProducerTemplate(
            KafkaProperties kafkaProperties) {
        Map<String, Object> producerProperties = new HashMap<>(kafkaProperties.buildProducerProperties());
        producerProperties.putIfAbsent("key.serializer", StringSerializer.class);
        producerProperties.putIfAbsent("value.serializer", JsonSerializer.class);

        SenderOptions<String, WebEvent> senderOptions = SenderOptions.<String, WebEvent>create(producerProperties)
                .withKeySerializer(new StringSerializer())
                .withValueSerializer(new JsonSerializer<>());
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }
}

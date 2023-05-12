package com.gtpuser.gtpuser.kafka;

import com.gtpuser.gtpuser.kafka.event.UserEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

@Configuration
public class UserSideConfig {
    @Bean
    public SenderOptions<String, UserEvent> senderOptions(KafkaProperties kafkaProperties){
        return SenderOptions.<String, UserEvent>create(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, UserEvent> producerTemplate(SenderOptions<String, UserEvent> options){
        return new ReactiveKafkaProducerTemplate<>(options);
    }
}
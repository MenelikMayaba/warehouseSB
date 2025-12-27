package com.aCompany.wms.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import jakarta.jms.Queue;

@Configuration
@EnableJms
public class JmsConfig {
    @Bean
    public Queue dispatchQueue() {
        return new ActiveMQQueue("dispatchQueue");
    }
}
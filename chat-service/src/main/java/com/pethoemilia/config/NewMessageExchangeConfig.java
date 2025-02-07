package com.pethoemilia.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NewMessageExchangeConfig {

	@Bean(name = "newMessageExchange")
	TopicExchange addExchange() {
		return new TopicExchange("newMessageExchange", true, false);
	}

}
package com.pethoemilia.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitQueueConfig {

	@Bean(name = "rabbitBean")
	Queue createQueue() {
		return new Queue("rabbit", true, false, false);
	}

	@Bean(name = "rabbitExchange")
	FanoutExchange addExchange() {
		return new FanoutExchange("rabbitExchange", true, false);
	}

	@Bean("rabbitBinding")
	Binding binding(@Qualifier("rabbitBean") Queue queue, @Qualifier("rabbitExchange") FanoutExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange);
	}

}
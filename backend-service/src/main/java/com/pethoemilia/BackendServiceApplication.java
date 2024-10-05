package com.pethoemilia;

import java.util.Date;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableScheduling
@ComponentScan({ "pethoemilia" })
public class BackendServiceApplication {
	
	@Autowired
	private RabbitTemplate template;
	private static int counter = 0;

	public static void main(String[] args) {
		SpringApplication.run(BackendServiceApplication.class, args);
	}
	@Scheduled(fixedDelay = 10000)
	public void sendMessageInQueue() {

		com.pethoemilia.entity.Message mmmmm = new com.pethoemilia.entity.Message();
		ObjectMapper mapper = new ObjectMapper();
		try {
			byte[] messageBytes = mapper.writeValueAsBytes(mmmmm);
			Message amqpMessage = MessageBuilder.withBody(messageBytes)
					.setContentType(MessageProperties.CONTENT_TYPE_JSON).build();
			this.template.convertAndSend("rabbitExchange", "rabbitExchange", amqpMessage, m -> {
				m.getMessageProperties().setPriority(3);
				m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
				m.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
				m.getMessageProperties().setExpiration(String.valueOf(24 * 60 * 60 * 1000));
				return m;
			});
			this.template.setMandatory(true);
		} catch (JsonProcessingException e) {
		}
	}

}

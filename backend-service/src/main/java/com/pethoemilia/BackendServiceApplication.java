package com.pethoemilia;

import java.util.Date;
import java.util.Random;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@EnableScheduling
@ComponentScan({ "com.pethoemilia" })
@RequiredArgsConstructor
public class BackendServiceApplication {

	private final RabbitTemplate template;
	private final RabbitAdmin rabbitAdmin;
	private final TopicExchange exchange;
	private static int counter = 0;
	private Random rand = new Random();

	public static void main(String[] args) {
		SpringApplication.run(BackendServiceApplication.class, args);
	}

	@Scheduled(fixedDelay = 5000)
	public void sendMessageInQueue() {
		String receiver = rand.nextInt() % 2 == 0 ? "Rabbot_01" : "Rabbot_02";
		com.pethoemilia.dto.RabbitMessage mmmmm = buildMessage(receiver);
		QueueInformation Rabbot_01QueueInfo = rabbitAdmin.getQueueInfo("Rabbot_01");
		if (Rabbot_01QueueInfo == null) {
			Queue queue = new Queue("Rabbot_01", true, false, false);
			rabbitAdmin.declareQueue(queue);
			rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with("Rabbot_01"));
		}
		QueueInformation Rabbot_02QueueInfo = rabbitAdmin.getQueueInfo("Rabbot_02");
		if (Rabbot_02QueueInfo == null) {
			Queue queue = new Queue("Rabbot_02", true, false, false);
			rabbitAdmin.declareQueue(queue);
			rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with("Rabbot_02"));
		}

		ObjectMapper mapper = new ObjectMapper();
		try {
			byte[] messageBytes = mapper.writeValueAsBytes(mmmmm);
			Message amqpMessage = MessageBuilder.withBody(messageBytes)
					.setContentType(MessageProperties.CONTENT_TYPE_JSON).build();
			this.template.convertAndSend(exchange.getName(), receiver, amqpMessage, m -> {
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

	private com.pethoemilia.dto.RabbitMessage buildMessage(String receiver) {
		com.pethoemilia.dto.RabbitMessage mmmmm = new com.pethoemilia.dto.RabbitMessage();
		mmmmm.setMessage("Rabbit test message");
		mmmmm.setSender("Rabbit♥" + counter++);
		mmmmm.setReceiver(receiver);
		mmmmm.setTimestamp(new Date().getTime());
		return mmmmm;
	}

}

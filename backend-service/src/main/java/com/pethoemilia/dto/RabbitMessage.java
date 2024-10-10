package com.pethoemilia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RabbitMessage {

	private String sender;
	private String receiver;
	private String message;
	private Long timestamp;

}

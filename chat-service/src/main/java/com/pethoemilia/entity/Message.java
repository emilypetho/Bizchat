package com.pethoemilia.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

	@Id
	@Column(name = "ID", unique = true, nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String content;

//	@Column(nullable = false)
////	@CreationTimestamp
////	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss:SSS")
//	private Date timestamp;

	@CreationTimestamp
	@Column(name = "timestamp", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date timestamp;

	@ManyToOne(targetEntity = Group.class)
	@JoinColumn(name = "group_id", referencedColumnName = "id")
//	@JsonProperty(access = Access.WRITE_ONLY)
	private Group group;

	@ManyToOne
	@JoinColumn(name = "sender_id", referencedColumnName = "id")
	private User sender;

	@JsonProperty("timestamp")
	public Long getTimestamp() {
		return timestamp.getTime();
	}
}

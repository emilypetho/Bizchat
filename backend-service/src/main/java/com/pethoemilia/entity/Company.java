package com.pethoemilia.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "company")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "users" })
public class Company {

	@Id
	@Column(name = "ID", unique = true, nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Nonnull
	@NotEmpty(message = "The name field can't be empty")
	@Size(max = 100, message = "The name field can't exceed 100 characters")
	@Column(unique = true)
	private String name;

	@Size(max = 255, message = "The address field can't exceed 255 characters")
	private String address;

	@OneToMany(targetEntity = User.class, mappedBy = "company", fetch = FetchType.LAZY, cascade = {
			CascadeType.REMOVE })
	private List<User> users;
}

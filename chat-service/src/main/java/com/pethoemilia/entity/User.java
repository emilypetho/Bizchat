package com.pethoemilia.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

	@Id
	@Column(name = "ID", unique = true, nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Nonnull
	@NotEmpty(message = "The email field can't be empty")
	@Email(message = "Please provide a valid email address")
	@Column(unique = true)
	private String email;

	@Nonnull
	@NotEmpty(message = "The name field can't be empty")
	@Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
	private String name;

	@Size(max = 100, message = "Address can be up to 100 characters")
	private String address;

	@Nonnull
	@NotEmpty(message = "The phone number field can't be empty")
	@Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Please provide a valid phone number")
	@Column(unique = true)
	private String phone_number;

	@ManyToOne(targetEntity = Company.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	private Company company;

//	@Nonnull
//	@NotEmpty(message = "The password field can't be empty")
//	@Size(min = 8, message = "Password must be at least 8 characters long")
//	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#+\\-_.])[A-Za-z\\d@$!%*?&#+\\-_.]{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
//	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
//	private String password;

	@Nonnull
	@NotEmpty(message = "The password field can't be empty")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	private String password;

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	public enum Gender {
		MAN, WOMAN, OTHER
	}

	@Nonnull
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private Gender gender;

	@ManyToMany(targetEntity = Group.class, mappedBy = "users", cascade = CascadeType.REMOVE)
	@JsonIgnore
	Set<Group> groupk;
}
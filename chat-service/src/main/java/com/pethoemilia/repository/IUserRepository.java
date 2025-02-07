package com.pethoemilia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pethoemilia.entity.User;

public interface IUserRepository extends JpaRepository<User, Long> { // Automatikusan generalt JPA repository, ami a
	// User entitas kezelesere szolgal az
	// adatbazisban, sok metodus van benne, pl.
	// save, delete, findById, findAll, stb.

	//User findByEmail(String email);
	
	Optional<User> findByEmail(String email);
}

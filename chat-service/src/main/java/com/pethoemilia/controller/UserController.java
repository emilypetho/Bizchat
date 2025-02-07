package com.pethoemilia.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pethoemilia.entity.User;
import com.pethoemilia.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping(value = "/findAll", produces = "application/json")
	@Transactional
	public ResponseEntity<List<User>> findAll() {
		return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
	}

	@PostMapping(value = "/save", produces = "application/json", consumes = "application/json")
	@Transactional
	public ResponseEntity<User> save(@RequestBody @Validated User user) {
		return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
	}

	@DeleteMapping(value = "/delete/{id}", produces = "application/json")
	@Transactional
	public ResponseEntity<String> delete(@PathVariable(name = "id") Long id) {
		userService.delete(id);
		return new ResponseEntity<>("Sikeresen torolve", HttpStatus.OK);
	}

	@GetMapping(value = "/findById/{id}", produces = "application/json")
	@Transactional
	public ResponseEntity<User> findById(@PathVariable(name = "id") Long id) {
		return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
	}

	@GetMapping(value = "/findByEmail/{email}", produces = "application/json")
	@Transactional
	public ResponseEntity<User> findByEmail(@PathVariable(name = "email") String email) {
	    Optional<User> optionalUser = userService.findByEmail(email);
	    if (optionalUser.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	    return new ResponseEntity<>(optionalUser.get(), HttpStatus.OK);
	}

	
	@PostMapping(value = "/register", produces = "application/json", consumes = "application/json")
	@Transactional
	public ResponseEntity<User> register(@RequestBody User user) {
		return new ResponseEntity<>(userService.save(user), HttpStatus.OK);
	}
//
//	@GetMapping(value = "/checkuser", produces = "plain/text")
//	@Transactional
//	public ResponseEntity<User> login() {
//		return new ResponseEntity<>("Success", HttpStatus.OK);
//	}
//	
//	@GetMapping(value = "/checkuser", produces = "application/json")
//	@Transactional
//	public ResponseEntity<Map<String, String>> login() {
//	    Map<String, String> response = new HashMap<>();
//	    response.put("status", "Success");
//	    return new ResponseEntity<>(response, HttpStatus.OK);
//	}

}

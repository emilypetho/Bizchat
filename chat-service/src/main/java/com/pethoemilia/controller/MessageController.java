package com.pethoemilia.controller;

import java.util.List;

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

import com.pethoemilia.entity.Message;
import com.pethoemilia.service.MessageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

	private final MessageService messageService;

	@GetMapping(value = "/findAll", produces = "application/json")
	@Transactional
	public ResponseEntity<List<Message>> findAll() {
		return new ResponseEntity<>(messageService.findAll(), HttpStatus.OK);
	}

	@PostMapping(value = "/save", produces = "application/json", consumes = "application/json")
	@Transactional
	public ResponseEntity<Message> save(@RequestBody @Validated Message message) {
		return new ResponseEntity<>(messageService.save(message), HttpStatus.CREATED);
	}

	@DeleteMapping(value = "/delete/{id}", produces = "application/json")
	@Transactional
	public ResponseEntity<String> delete(@PathVariable(name = "id") Long id) {
		messageService.delete(id);
		return new ResponseEntity<>("Sikeresen torolve", HttpStatus.OK);
	}

	@GetMapping(value = "/findById/{id}", produces = "application/json")
	@Transactional
	public ResponseEntity<Message> findById(@PathVariable(name = "id") Long id) {
		return new ResponseEntity<>(messageService.findById(id), HttpStatus.OK);
	}

	@GetMapping(value = "/findByGroupId/{id}", produces = "application/json")
	@Transactional
	public ResponseEntity<List<Message>> findByGroupId(@PathVariable(name = "id") Long id) {
		return new ResponseEntity<>(messageService.findByGroupId(id), HttpStatus.OK);
	}

}

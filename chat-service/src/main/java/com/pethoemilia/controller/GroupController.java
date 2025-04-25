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

import com.pethoemilia.entity.Group;
import com.pethoemilia.service.GroupService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {

	private final GroupService groupService;

	@GetMapping(value = "/findAll", produces = "application/json")
	@Transactional
	public ResponseEntity<List<Group>> findAll() {
		List<Group> groups = groupService.findAll();
		return new ResponseEntity<>(groups, HttpStatus.OK);
	}

	@PostMapping(value = "/save", produces = "application/json", consumes = "application/json")
	@Transactional
	public ResponseEntity<Group> save(@RequestBody @Validated Group group) {
		return new ResponseEntity<>(groupService.save(group), HttpStatus.CREATED);
	}

	@DeleteMapping(value = "/delete/{id}", produces = "application/json")
	@Transactional
	public ResponseEntity<String> delete(@PathVariable(name = "id") Long id) {
		groupService.delete(id);
		return new ResponseEntity<>("Sikeresen torolve", HttpStatus.OK);
	}

	@GetMapping(value = "/findById/{id}", produces = "application/json")
	@Transactional
	public ResponseEntity<Group> findById(@PathVariable(name = "id") Long id) {
		return new ResponseEntity<>(groupService.findById(id), HttpStatus.OK);
	}

	@GetMapping(value = "/findByUserId/{id}", produces = "application/json")
	@Transactional
	public ResponseEntity<List<Group>> findByUserId(@PathVariable(name = "id") Long id) {
		return new ResponseEntity<>(groupService.findByUserId(id), HttpStatus.OK);
	}

	@GetMapping(value = "/summarize/{id}", produces = "application/json")
	@Transactional
	public ResponseEntity<String> summarize(@PathVariable(name = "id") Long id) {
		return new ResponseEntity<>(groupService.summarize(id), HttpStatus.OK);
	}

}

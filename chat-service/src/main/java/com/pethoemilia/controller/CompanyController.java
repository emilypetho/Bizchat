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

import com.pethoemilia.entity.Company;
import com.pethoemilia.service.CompanyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

	private final CompanyService companyService;

	@GetMapping(value = "/findAll", produces = "application/json")
	@Transactional
	public ResponseEntity<List<Company>> findAll() {
		return new ResponseEntity<>(companyService.findAll(), HttpStatus.OK);
	}

	@PostMapping(value = "/save", produces = "application/json", consumes = "application/json")
	@Transactional
	public ResponseEntity<Company> save(@RequestBody @Validated Company company) {
		return new ResponseEntity<>(companyService.save(company), HttpStatus.CREATED);
	}

	@DeleteMapping(value = "/delete/{id}", produces = "application/json")
	@Transactional
	public ResponseEntity<String> delete(@PathVariable(name = "id") Long id) {
		companyService.delete(id);
		return new ResponseEntity<>("Sikerresen torolve", HttpStatus.OK);
	}

	@GetMapping(value = "/findById/{id}", produces = "application/json")
	@Transactional
	public ResponseEntity<Company> findById(@PathVariable(name = "id") Long id) {
		return new ResponseEntity<>(companyService.findById(id), HttpStatus.OK);
	}

	@GetMapping(value = "/findByName/{name}", produces = "application/json")
	@Transactional
	public ResponseEntity<Long> findByName(@PathVariable(name = "name") String name) {
		Long id = companyService.findByName(name);
		if (id == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(id, HttpStatus.OK);
	}

//	@GetMapping(value = "/findByName/{name}", produces = "application/json")
//	@Transactional
//	public ResponseEntity<Long> findByName(@PathVariable String name) {
//		Company company = companyService.findByName(name);
//		if (company == null || company.getId() == null) {
//			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//		}
//		return new ResponseEntity<>(company.getId(), HttpStatus.OK);
//	}

}

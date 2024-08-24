package com.pethoemilia.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pethoemilia.entity.Company;
import com.pethoemilia.repository.ICompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

	private final ICompanyRepository companyRepo;

	public Company save(Company company) {
		return companyRepo.save(company);
	}

	public void delete(Long id) {
		companyRepo.deleteById(id);
	}

	public Company findById(Long id) {
		return companyRepo.findById(id).orElse(null);
	}

	public List<Company> findAll() {
		return companyRepo.findAll();
	}

	public Long findByName(String name) {
		return companyRepo.findByName(name);
	}

}

package com.pethoemilia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pethoemilia.entity.Company;

public interface ICompanyRepository extends JpaRepository<Company, Long> {
	@Query("SELECT c.id FROM Company c WHERE c.name = :name")
	Long findByName(@Param("name") String name);

//	@Query("SELECT c FROM Company c WHERE c.name = :name")
//	Company findByName(@Param("name") String name);
}

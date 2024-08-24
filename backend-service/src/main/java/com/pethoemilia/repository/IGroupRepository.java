package com.pethoemilia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pethoemilia.entity.Group;

public interface IGroupRepository extends JpaRepository<Group, Long> {

	@Query("select distinct e from #{#entityName} e join e.users u where u.id = :userId")
	List<Group> findByUserId(@Param("userId") Long userId);

}

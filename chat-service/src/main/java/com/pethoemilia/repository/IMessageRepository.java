package com.pethoemilia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pethoemilia.entity.Message;

public interface IMessageRepository extends JpaRepository<Message, Long> {
	@Query("select distinct e from #{#entityName} e join e.group u where u.id = :group_id") // rendezni ido szerint
	List<Message> findByGroupId(@Param("group_id") Long group_id);
}

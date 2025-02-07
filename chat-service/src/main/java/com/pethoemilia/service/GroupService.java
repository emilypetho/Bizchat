package com.pethoemilia.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pethoemilia.entity.Group;
import com.pethoemilia.repository.IGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupService {

	private final IGroupRepository groupRepo;

	public Group save(Group group) {
		return groupRepo.save(group);
	}

	public void delete(Long id) {
		groupRepo.deleteById(id);
	}

	public Group findById(Long id) {
		return groupRepo.findById(id).orElse(null);
	}

	public List<Group> findAll() {
		return groupRepo.findAll();
	}

	public List<Group> findByUserId(Long userId) {
		return groupRepo.findByUserId(userId);
	}

}

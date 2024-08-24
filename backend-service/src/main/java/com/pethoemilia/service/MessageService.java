package com.pethoemilia.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pethoemilia.entity.Message;
import com.pethoemilia.repository.IMessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

	private final IMessageRepository messageRepo; // Az IUserRepository interfesz implementacioja

	public Message save(Message message) {
		return messageRepo.save(message);
	}

	public void delete(Long id) {
		messageRepo.deleteById(id);
	}

	public Message findById(Long id) {
		return messageRepo.findById(id).orElse(null);
	}

	public List<Message> findAll() {
		return messageRepo.findAll();
	}

	public List<Message> findByGroupId(Long group_id) {
		return messageRepo.findByGroupId(group_id);
	}

}

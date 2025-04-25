package com.pethoemilia.service;

import java.util.List;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import com.pethoemilia.entity.Group;
import com.pethoemilia.entity.Message;
import com.pethoemilia.repository.IGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupService {

	private final OpenAiChatModel chatModel;
	private final IGroupRepository groupRepo;
	private final MessageService messageService;

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

	public String summarize(Long id) {
		List<Message> messages = messageService.findByGroupId(id);
		StringBuilder sb = new StringBuilder();
		for (Message message : messages) {
			sb.append(message.getSender().getName() + ":");
			sb.append(message.getContent() + "\n");
		}
		ChatResponse response = chatModel.call(new Prompt("Foglald ossze: \"" + sb.toString() + "\"",
				OpenAiChatOptions.builder().model("llama-3.3-70b-versatile").temperature(0.4).build()));
		response.toString();
		var resultList = response.getResults();
		StringBuilder responses = new StringBuilder();
		for (var result : resultList) {
			responses.append(result.getOutput().getText());
		}
		return responses.toString();
	}

}

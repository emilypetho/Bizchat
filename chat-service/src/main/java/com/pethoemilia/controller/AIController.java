package com.pethoemilia.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pethoemilia.service.DeepSeekService;

@RestController
@RequestMapping("/api/ai")
public class AIController {
	private final DeepSeekService deepSeekService;

	public AIController(DeepSeekService deepSeekService) {
		this.deepSeekService = deepSeekService;
	}

	@PostMapping("/generate")
	public String generate(@RequestBody String request) {
		return deepSeekService.generateText(request);
	}
}

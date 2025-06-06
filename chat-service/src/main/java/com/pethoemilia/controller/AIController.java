package com.pethoemilia.controller;

import java.util.Map;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pethoemilia.service.DeepSeekService;

import lombok.RequiredArgsConstructor;

//@RestController
//@RequestMapping("/api/ai")
//public class AIController {
//	private final DeepSeekService deepSeekService;
//
//	public AIController(DeepSeekService deepSeekService) {
//		this.deepSeekService = deepSeekService;
//	}
//
//	@PostMapping("/generate")
//	public String generate(@RequestBody String request) {
//		return deepSeekService.generateText(request);
//	}
//}
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final OpenAiChatModel chatModel;

    @PostMapping("/generate")
    public ResponseEntity<String> generateResponse(@RequestBody Map<String, String> request) {
        String input = request.get("input");

        Prompt prompt = new Prompt("Válaszolj erre a kérdésre magyarul: " + input);
        ChatResponse response = chatModel.call(prompt);
        String content = response.getResult().getOutput().getContent();

        return ResponseEntity.ok(content);
    }
}

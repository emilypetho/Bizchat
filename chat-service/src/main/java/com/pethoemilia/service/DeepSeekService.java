package com.pethoemilia.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DeepSeekService {
	private final String API_URL = "https://api.deepseek.com/generate";

	public String generateText(String input) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer sk-bb459fd015994ebeb372f6e27931deea");

		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("prompt", input);
		requestBody.put("max_tokens", "100");

		HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);

		return response.getBody();
	}
}

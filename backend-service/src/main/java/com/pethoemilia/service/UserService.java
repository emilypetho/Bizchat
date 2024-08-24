package com.pethoemilia.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pethoemilia.entity.User;
import com.pethoemilia.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final IUserRepository userRepository; // Az IUserRepository interfesz implementacioja

	public User save(User user) {
		return userRepository.save(user);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public void delete(Long id) {
		userRepository.deleteById(id);
	}

//	public void delete(Long id) {
//		Optional<User> userOptional = userRepository.findById(id);
//		if (userOptional.isPresent()) {
//			User user = userOptional.get();
//			user.removeAllGroups();
//			userRepository.save(user); // Save the user after removing all groups
//			userRepository.deleteById(id);
//		}
//	}

	public User findById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

}

package com.pethoemilia.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pethoemilia.entity.User;
import com.pethoemilia.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{

	private final IUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public User save(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    Optional<User> user = userRepository.findByEmail(username);
	    if (!user.isPresent()) {
	        throw new UsernameNotFoundException(username);
	    }
	    return org.springframework.security.core.userdetails.User.builder()
	            .username(user.get().getEmail())
	            .password(user.get().getPassword())
	            .roles("USER")
	            .build();
	}

	public Optional<User> findByEmail(String email) {
	    return userRepository.findByEmail(email);
	}

	public void delete(Long id) {
		userRepository.deleteById(id);
	}

	public User findById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

}

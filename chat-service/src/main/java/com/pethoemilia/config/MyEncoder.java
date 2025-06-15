package com.pethoemilia.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MyEncoder {
//	@Bean
//	PasswordEncoder passwordEncoder() {
//		Map<String, PasswordEncoder> encoders = new HashMap<>();
//		encoders.put("", new BCryptPasswordEncoder());
//		encoders.put("bcrypt", new BCryptPasswordEncoder());
//		return new DelegatingPasswordEncoder("bcrypt", encoders);
//	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
	    Map<String, PasswordEncoder> encoders = new HashMap<>();
	    Argon2PasswordEncoder argon2Encoder = new Argon2PasswordEncoder(
	        16,    // saltLength
	        32,    // hashLength
	        1,     // parallelism
	        65536, // memory in KB = 64MB
	        3      // iterations
	    );

	    encoders.put("argon2", argon2Encoder);

	    return new DelegatingPasswordEncoder("argon2", encoders);
	}

}

package com.caon.java_web_service.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caon.java_web_service.entities.User;

@RestController
@RequestMapping(value = "/users")
public class UserResource {
	
	@GetMapping
	public ResponseEntity<User> findAll() {
		User u = new User(1L, "João", "joao123@gmail.com", "11 99452 1010", "Dojw234hn#@d");
		return ResponseEntity.ok().body(u);
	}
}

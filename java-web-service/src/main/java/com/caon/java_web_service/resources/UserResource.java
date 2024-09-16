package com.caon.java_web_service.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caon.java_web_service.entities.User;
import com.caon.java_web_service.services.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserResource {
	@Autowired
	private UserService userService;
	@GetMapping
	public ResponseEntity<List<User>> findAll() {
		List<User> users = userService.findAll();
		return ResponseEntity.ok().body(users);
	}
	@GetMapping("/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		User u = userService.findById(id);
		return ResponseEntity.ok().body(u);
	}
}

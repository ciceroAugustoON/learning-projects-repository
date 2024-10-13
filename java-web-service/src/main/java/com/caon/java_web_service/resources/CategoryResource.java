package com.caon.java_web_service.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caon.java_web_service.entities.Category;
import com.caon.java_web_service.services.CategoryService;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {
	@Autowired
	private CategoryService CategoryService;
	@GetMapping
	public ResponseEntity<List<Category>> findAll() {
		List<Category> Categorys = CategoryService.findAll();
		return ResponseEntity.ok().body(Categorys);
	}
	@GetMapping("/{id}")
	public ResponseEntity<Category> findById(@PathVariable Long id) {
		Category u = CategoryService.findById(id);
		return ResponseEntity.ok().body(u);
	}
}

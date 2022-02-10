package com.rest.mongo.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rest.mongo.entities.User;
import com.rest.mongo.entities.UserInfo;
import com.rest.mongo.repositories.UserRepository;
import com.rest.mongo.repositories.UserTemplate;

@RestController
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserTemplate userTemplate;

	@GetMapping(value = "/users")
	public ResponseEntity<List<User>> listUsers(
			@RequestParam(name = "userInfo.name", required = false) String userInfoName) {

		LOGGER.info(userInfoName);

		User exampleUser = new User();
		UserInfo userInfo = new UserInfo();
		userInfo.setName(userInfoName);
		exampleUser.setUserInfo(userInfo);

		List<User> listUser = null;
		if (userInfoName != null) {
			listUser = userTemplate.listUsersByName(userInfoName);
		} else {
			listUser = userRepository.findAll();
		}

		ResponseEntity<List<User>> response = null;
		if (listUser == null || listUser.isEmpty()) {
			response = new ResponseEntity<List<User>>(null, null, HttpStatus.NO_CONTENT);
		} else {
			response = new ResponseEntity<List<User>>(listUser, null, HttpStatus.OK);
		}

		return response;
	}

	@GetMapping(value = "/users/{id}")
	public ResponseEntity<User> getUser(@PathVariable(name = "id", required = true) String id) {

		LOGGER.info(id);

		User foundUser = userTemplate.getUser(id);

		ResponseEntity<User> response = null;
		if (foundUser == null) {
			response = new ResponseEntity<User>(null, null, HttpStatus.NOT_FOUND);
		} else {
			response = new ResponseEntity<User>(foundUser, null, HttpStatus.OK);
		}

		return response;
	}

	@PostMapping(value = "/users")
	public ResponseEntity<Object> createUser(@RequestHeader Map<String, String> headers, @RequestBody User payload) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Custom-Header", "foo");
		responseHeaders.add("Content-Type", "application/json");

		ResponseEntity<Object> response = null;
		try {
			User createdUser = userRepository.insert(payload);
			response = new ResponseEntity<Object>(createdUser, responseHeaders, HttpStatus.CREATED);
		} catch (DuplicateKeyException e) {
			response = new ResponseEntity<Object>("{\"error\": \"id already exists\"}", responseHeaders,
					HttpStatus.BAD_REQUEST);
		}

		return response;
	}

	@PutMapping(value = "/users/{id}")
	public ResponseEntity<User> insertUser(@PathVariable(name = "id", required = true) String id,
			@RequestBody User payload) {
		User user = userTemplate.insertUser(id, payload);

		ResponseEntity<User> response = null;
		if (user == null) {
			response = new ResponseEntity<User>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			response = new ResponseEntity<User>(user, null, HttpStatus.OK);
		}
		return response;
	}

	@PatchMapping(value = "/users/{id}")
	public ResponseEntity<User> updateUser(@PathVariable(name = "id", required = true) String id,
			@RequestBody User payload) {
		User foundUser = userTemplate.getUser(id);
		User updatedUser = null;

		ResponseEntity<User> response = null;
		if (foundUser == null) {
			response = new ResponseEntity<User>(null, null, HttpStatus.NOT_FOUND);
		} else {
			updatedUser = User.updateUser(foundUser, payload);

			updatedUser = userTemplate.insertUser(id, foundUser);
			if (updatedUser == null) {
				response = new ResponseEntity<User>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				response = new ResponseEntity<User>(updatedUser, null, HttpStatus.OK);
			}
		}
		return response;
	}

	@DeleteMapping(value = "/users/{id}")
	public ResponseEntity<User> deleteUser(@PathVariable(name = "id", required = true) String id) {
		boolean result = userTemplate.deleteUser(id);
		
		ResponseEntity<User> response = null;
		if (result == true) {
			response = new ResponseEntity<User>(null, null, HttpStatus.NO_CONTENT);
		} else {
			response = new ResponseEntity<User>(null, null, HttpStatus.NOT_FOUND);
		}
		return response;
	}

}

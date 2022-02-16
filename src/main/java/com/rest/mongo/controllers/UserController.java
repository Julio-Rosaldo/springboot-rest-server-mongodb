package com.rest.mongo.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.rest.mongo.daos.UserTemplate;
import com.rest.mongo.entities.ResponseData;
import com.rest.mongo.entities.ResponseListData;
import com.rest.mongo.entities.User;

@RestController
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserTemplate userTemplate;

	@GetMapping(value = "/users")
	public ResponseEntity<ResponseListData> listUsers(HttpServletRequest request,
			@RequestParam(name = "paginationKey", required = false) Long paginationKey,
			@RequestParam(name = "pageSize", required = false) Long pageSize,
			@RequestParam(name = "userInfo.name", required = false) String userInfoName) {

		LOGGER.info(request.getLocalAddr());
		LOGGER.info(request.getRemoteAddr());

		paginationKey = paginationKey == null ? 0L : paginationKey;
		pageSize = pageSize == null ? 10L : pageSize;

		ResponseListData data = userTemplate.listUsers(paginationKey, pageSize, userInfoName);

		HttpStatus status = null;
		if (data.getError() != null) {
			status = data.getError().getCode();
		} else if (data.getData() == null || data.getData().isEmpty()) {
			status = HttpStatus.NO_CONTENT;
		} else {
			status = HttpStatus.OK;
		}

		ResponseEntity<ResponseListData> response = new ResponseEntity<>(data, null, status);
		return response;
	}

	@GetMapping(value = "/users/{id}")
	public ResponseEntity<ResponseData> getUser(@PathVariable(name = "id", required = true) String id) {

		ResponseData data = userTemplate.getUser(id);

		HttpStatus status = null;
		if (data.getError() != null) {
			status = data.getError().getCode();
		} else if (data.getData() == null) {
			status = HttpStatus.NO_CONTENT;
		} else {
			status = HttpStatus.OK;
		}

		ResponseEntity<ResponseData> response = new ResponseEntity<ResponseData>(data, null, status);
		return response;
	}

	@PostMapping(value = "/users")
	public ResponseEntity<ResponseData> createUser(@RequestHeader Map<String, String> headers,
			@RequestBody User payload) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Custom-Header", "foo");
		responseHeaders.add("Content-Type", "application/json");

		ResponseData data = userTemplate.createUser(payload);

		HttpStatus status = null;
		if (data.getError() != null) {
			status = data.getError().getCode();
		} else {
			status = HttpStatus.CREATED;
		}

		ResponseEntity<ResponseData> response = new ResponseEntity<ResponseData>(data, responseHeaders, status);
		return response;
	}

	@PutMapping(value = "/users/{id}")
	public ResponseEntity<ResponseData> insertUser(@PathVariable(name = "id", required = true) String id,
			@RequestBody User payload) {

		ResponseData data = userTemplate.getUser(id);
		HttpStatus status = null;
		if (data.getData() != null) {
			User updatedUser = User.updateUser(data.getData(), payload);
			data = userTemplate.updateUser(id, updatedUser);
			status = HttpStatus.OK;
		} else {
			data = userTemplate.updateUser(id, payload);
			status = HttpStatus.CREATED;
		}

		if (data.getError() != null) {
			status = data.getError().getCode();
		}

		ResponseEntity<ResponseData> response = new ResponseEntity<ResponseData>(data, null, status);
		return response;
	}

	@PatchMapping(value = "/users/{id}")
	public ResponseEntity<ResponseData> updateUser(@PathVariable(name = "id", required = true) String id,
			@RequestBody User payload) {

		ResponseData data = userTemplate.getUser(id);
		if (data.getData() != null) {
			User updatedUser = User.updateUser(data.getData(), payload);
			data = userTemplate.updateUser(id, updatedUser);
		}

		HttpStatus status = null;
		if (data.getError() != null) {
			status = data.getError().getCode();
		} else {
			status = HttpStatus.OK;
		}

		ResponseEntity<ResponseData> response = new ResponseEntity<ResponseData>(data, null, status);
		return response;
	}

	@DeleteMapping(value = "/users")
	public ResponseEntity<ResponseData> deleteUsers() {
		ResponseData data = userTemplate.deleteUsers();

		HttpStatus status = null;
		if (data.getError() != null) {
			status = data.getError().getCode();
		} else {
			status = HttpStatus.NO_CONTENT;
		}

		ResponseEntity<ResponseData> response = new ResponseEntity<ResponseData>(data, null, status);
		return response;
	}

	@DeleteMapping(value = "/users/{id}")
	public ResponseEntity<ResponseData> deleteUser(@PathVariable(name = "id", required = true) String id) {
		ResponseData data = userTemplate.deleteUser(id);

		HttpStatus status = null;
		if (data.getError() != null) {
			status = data.getError().getCode();
		} else {
			status = HttpStatus.NO_CONTENT;
		}

		ResponseEntity<ResponseData> response = new ResponseEntity<ResponseData>(data, null, status);
		return response;
	}

}

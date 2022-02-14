package com.rest.mongo.daos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.DeleteResult;
import com.rest.mongo.entities.ResponseData;
import com.rest.mongo.entities.ResponseError;
import com.rest.mongo.entities.ResponseListData;
import com.rest.mongo.entities.User;

//Esta interfaz sirve para crear funcionalidades a medida, enfocadas sobre todo a la busqueda y actualizacion
//de informacion, apoyandose del bean mongoTemplate
@Repository
public class UserTemplateImpl implements UserTemplate {

	@Autowired
	UserRepository userRepository;

	@Autowired
	MongoTemplate mongoTemplate;
	
	@Override
	public ResponseListData listUsers() {
		ResponseListData data = new ResponseListData();
		
		List<User> listUsers = userRepository.findAll();
		data.setData(listUsers);
		
		return data;
	}

	@Override
	public ResponseListData listUsersByName(String userInfoName) {
		ResponseListData data = new ResponseListData();
		
		Query query = new Query();
		query.addCriteria(Criteria.where("userInfo.name").is(userInfoName));
		List<User> listUsers = mongoTemplate.find(query, User.class, "users");
		data.setData(listUsers);
		
		return data;
	}

	@Override
	public ResponseData getUser(String id) {
		ResponseData data = new ResponseData();

		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		List<User> listUsers = mongoTemplate.find(query, User.class, "users");

		try {
			if (listUsers != null && !listUsers.isEmpty()) {
				data.setData(listUsers.get(0));
			} else {
				throw new EmptyResultDataAccessException(1);
			}
		} catch (EmptyResultDataAccessException e) {
			ResponseError error = new ResponseError();
			error.setName(e.getClass().getSimpleName());
			error.setMessage(e.getMessage());
			error.setCode(HttpStatus.NOT_FOUND);
			data.setError(error);
		}

		return data;
	}
	
	@Override
	public ResponseData createUser(User user) {
		ResponseData data = new ResponseData();

		try {
			User createdUser = userRepository.insert(user);
			data.setData(createdUser);
		} catch (DuplicateKeyException e) {
			ResponseError error = new ResponseError();
			error.setName(e.getClass().getSimpleName());
			error.setMessage(e.getMessage());
			error.setCode(HttpStatus.BAD_REQUEST);
			data.setError(error);
		} catch (IllegalArgumentException e) {
			ResponseError error = new ResponseError();
			error.setName(e.getClass().getSimpleName());
			error.setMessage(e.getMessage());
			error.setCode(HttpStatus.BAD_REQUEST);
			data.setError(error);
		}
		
		return data;
	}
	
	@Override
	public ResponseData updateUser(String id, User user) {
		ResponseData data = new ResponseData();

		try {
			user.setId(id);
			User createdUser = userRepository.save(user);
			data.setData(createdUser);
		} catch (IllegalArgumentException e) {
			ResponseError error = new ResponseError();
			error.setName(e.getClass().getSimpleName());
			error.setMessage(e.getMessage());
			error.setCode(HttpStatus.BAD_REQUEST);
			data.setError(error);
		}
		
		return data;
	}

	// El id es requerido para actualizar el elemento deseado
	/*
	public ResponseData insertUser(String id, User user) {
		ResponseData data = new ResponseData();
		
		try {
			user.setId(id);
			data.setData(mongoTemplate.save(user, "users"));
		} catch (IllegalArgumentException e) {
			ResponseError error = new ResponseError();
			error.setName(e.getClass().getSimpleName());
			error.setMessage(e.getMessage());
			error.setCode(HttpStatus.BAD_REQUEST);
			data.setError(error);
		}

		return data;
	}
	*/

	@Override
	public ResponseData deleteUser(String id) {
		ResponseData data = new ResponseData();

		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));

		try {
			DeleteResult deleteResult = mongoTemplate.remove(query, User.class, "users");
			Long count = deleteResult.getDeletedCount();
			if (!count.equals(1L)) {
				throw new EmptyResultDataAccessException(1);
			}
		} catch (IllegalArgumentException e) {
			ResponseError error = new ResponseError();
			error.setName(e.getClass().getSimpleName());
			error.setMessage(e.getMessage());
			error.setCode(HttpStatus.BAD_REQUEST);
			data.setError(error);
		} catch (EmptyResultDataAccessException e) {
			ResponseError error = new ResponseError();
			error.setName(e.getClass().getSimpleName());
			error.setMessage(e.getMessage());
			error.setCode(HttpStatus.NOT_FOUND);
			data.setError(error);
		}

		return data;
	}
}

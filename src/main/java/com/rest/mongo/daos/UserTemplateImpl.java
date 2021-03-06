package com.rest.mongo.daos;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.DeleteResult;
import com.rest.mongo.controllers.UserController;
import com.rest.mongo.entities.ResponseData;
import com.rest.mongo.entities.ResponseError;
import com.rest.mongo.entities.ResponseListData;
import com.rest.mongo.entities.User;

//Esta interfaz sirve para crear funcionalidades a medida, enfocadas sobre todo a la busqueda y actualizacion
//de informacion, apoyandose del bean mongoTemplate
@Repository
public class UserTemplateImpl implements UserTemplate {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	private static final String USERS_COLLECTION = "users";

	@Override
	public ResponseListData listUsers(Long page, Long pageSize, String email, String userInfoName) {
		ResponseListData data = new ResponseListData();

		page = page == null ? 0L : page;
		pageSize = pageSize == null ? 10L : pageSize;

		Query query = new Query();

		// Add find criteria
		if (email != null) {
			query.addCriteria(Criteria.where("email").is(email));
		}

		// Add find criteria
		if (userInfoName != null) {
			query.addCriteria(Criteria.where("userInfo.name").is(userInfoName));
		}

		// First, get total elements
		Long totalElements = mongoTemplate.count(query, User.class, USERS_COLLECTION);

		// Then, add pagination criteria
		query.skip(page * pageSize);
		query.limit(pageSize.intValue());

		// Finally, execute the query
		List<User> listUsers = mongoTemplate.find(query, User.class, USERS_COLLECTION);
		data.setData(listUsers);

		if (!listUsers.isEmpty()) {
			data.setPagination(UserConvertionHelper.createPagination(page, pageSize, totalElements));
		}

		return data;
	}

	@Override
	public ResponseData getUser(String id) {
		ResponseData data = new ResponseData();

		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		List<User> listUsers = mongoTemplate.find(query, User.class, USERS_COLLECTION);

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
	public ResponseData createUser(String id, User user) {
		ResponseData data = new ResponseData();

		try {
			if (id == null || id.isEmpty()) {
				id = UUID.randomUUID().toString();
			}
			// Check if the provided id has the right format
			else if (!UUID.fromString(id).toString().equals(id)) {
				throw new IllegalArgumentException("Invalid UUID string: ".concat(id));
			}
			user.setId(id);

			User createdUser = userRepository.insert(user);
			data.setData(createdUser);
		} catch (IllegalArgumentException e) {
			ResponseError error = new ResponseError();
			error.setName(e.getClass().getSimpleName());
			error.setMessage(e.getMessage());
			error.setCode(HttpStatus.BAD_REQUEST);
			data.setError(error);
		} catch (DuplicateKeyException e) {
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
			if (!UUID.fromString(id).toString().equals(id)) {
				throw new IllegalArgumentException("Invalid UUID string: ".concat(id));
			}
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

	@Override
	public ResponseData deleteUsers() {
		ResponseData data = new ResponseData();

		try {
			DeleteResult deleteResult = mongoTemplate.remove(new Query(), User.class, USERS_COLLECTION);
			Long count = deleteResult.getDeletedCount();
			if (count.equals(0L)) {
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

	@Override
	public ResponseData deleteUser(String id) {
		ResponseData data = new ResponseData();

		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));

		try {
			DeleteResult deleteResult = mongoTemplate.remove(query, User.class, USERS_COLLECTION);
			Long count = deleteResult.getDeletedCount();
			if (count.equals(0L)) {
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

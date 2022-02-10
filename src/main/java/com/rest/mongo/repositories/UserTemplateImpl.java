package com.rest.mongo.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.DeleteResult;
import com.rest.mongo.entities.User;

//Esta interfaz sirve para crear funcionalidades a medida, enfocadas sobre todo a la busqueda y actualizacion
//de informacion, apoyandose del bean mongoTemplate
@Repository
public class UserTemplateImpl implements UserTemplate {

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public List<User> listUsersByName(String userInfoName) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userInfo.name").is(userInfoName));
		List<User> listUsers = mongoTemplate.find(query, User.class, "users");
		return listUsers;
	}

	@Override
	public User getUser(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		List<User> listUsers = mongoTemplate.find(query, User.class, "users");

		User foundUser = null;
		if (listUsers != null && !listUsers.isEmpty()) {
			foundUser = listUsers.get(0);
		}
		return foundUser;
	}

	// El id es requerido para actualizar el elemento deseado
	@Override
	public User insertUser(String id, User user) {
		user.setId(id);
		user = mongoTemplate.save(user, "users");
		return user;
	}

	@Override
	public boolean deleteUser(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		DeleteResult deleteResult = mongoTemplate.remove(query, User.class, "users");
		
		Long count = deleteResult.getDeletedCount();
		if (count.equals(1L)) {
			return true;
		}
		else {
			return false;
		}		
	}
}

package com.rest.mongo.repositories;

import java.util.List;

import com.rest.mongo.entities.User;

//Esta interfaz sirve para crear funcionalidades a medida, enfocadas sobre todo a la busqueda y actualizacion
//de informacion, apoyandose del bean mongoTemplate
public interface UserTemplate {
	List<User> listUsersByName(String userInfoName);
	User getUser (String id);
	User insertUser(String id, User user);
	boolean deleteUser(String id);
}

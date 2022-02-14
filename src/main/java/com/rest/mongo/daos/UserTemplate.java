package com.rest.mongo.daos;

import com.rest.mongo.entities.ResponseData;
import com.rest.mongo.entities.ResponseListData;
import com.rest.mongo.entities.User;

//Esta interfaz sirve para crear funcionalidades a medida, enfocadas sobre todo a la busqueda y actualizacion
//de informacion, apoyandose del bean mongoTemplate
public interface UserTemplate {
	ResponseListData listUsers();

	ResponseListData listUsersByName(String userInfoName);

	ResponseData getUser(String id);
	
	ResponseData createUser(User user);

	ResponseData updateUser(String id, User user);

	ResponseData deleteUser(String id);
}

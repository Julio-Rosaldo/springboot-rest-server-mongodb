package com.rest.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rest.mongo.entities.User;

// Esta interfaz provee de funcionalidades comunes que hacen lo complejo sencillo, como busquedas 
// listAll, inserciones, y eliminaciones por id. Tiene la particularidad de que no requiere
// ser implementada, ya que spring automaticamente genera el bean de esta interfaz
@Repository
public interface UserRepository extends MongoRepository<User, String> {

}

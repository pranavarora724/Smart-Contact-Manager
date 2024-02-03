package com.spring.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.spring.entities.User;
import java.util.List;


public interface UserDao extends CrudRepository<User, Integer> 
{

	@Query("select u from User u where u.email =:email")
   public User findByUsername(@Param("email") String email);

}

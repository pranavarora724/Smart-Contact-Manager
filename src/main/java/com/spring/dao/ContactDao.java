package com.spring.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spring.entities.Contact;

import jakarta.transaction.Transactional;

public interface ContactDao extends JpaRepository<Contact, Integer>
{
	
//	We will return Page<Contact> instead of list<Contact>
//	We will pass  Pageable object inside
//	   PAGEABLE CONSISTS
//	1 - Max number of pages
//	2 - Current Page
	
	@Query("from Contact as c where c.user.id =:userId")
   public Page<Contact> findContactByUserId(@Param("userId")int userId , Pageable pageable);
	
	@Query("select d from Contact d where d.cid =:cid")
	public Contact findByContactId(@Param("cid") int cid);
	
	@Modifying
	@Transactional
	@Query("delete from Contact c where c.cid =:cid")
	void deleteContactById(@Param("cid") int cid);
	
}

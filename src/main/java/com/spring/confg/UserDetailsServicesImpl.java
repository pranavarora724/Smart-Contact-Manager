package com.spring.confg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.spring.dao.UserDao;
import com.spring.entities.User;

@Component
public class UserDetailsServicesImpl implements UserDetailsService
{

	@Autowired
	private UserDao userDao ;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
//		1 -  Fetch User Object By Username       (By Using UserDaoInterface)
//		2-   Convert it to UserDetails Object
//		3-   Return that UserDetails Object
		
		System.out.println("Inside UserDetails");
         User user = this.userDao.findByUsername(username);		
		System.out.println(username);
		System.out.println(user.getEmail());
		System.out.println(user.getPassword());
		
		if(user==null)
		 throw new UsernameNotFoundException("User Not Found"); 
		
		CustomerUserDetails customerUserDetails = new CustomerUserDetails(user);
		return customerUserDetails;
	}
  
}

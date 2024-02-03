package com.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.dao.UserDao;
import com.spring.entities.User;
import com.spring.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class Controllers 
{
	
	
	
//	Calling UserDao Function to store values in the database
	@Autowired
	private UserDao userDao;
	
//	Calling password encoder to store values in encrypted format
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@RequestMapping("/home")
	public String home()
	{
		System.out.println("This is home page");
		return "home";
	}

	@RequestMapping("/about")
	public String about()
	{
		System.out.println("This is about page");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model)
	{
		
		model.addAttribute("title" , "Signup");
		model.addAttribute("result" , new User());
		
		return "signup";
	}
	
	@RequestMapping(path="/doregister" , method =RequestMethod.POST)
	public String doregister(@ModelAttribute User user , 
			@RequestParam(value="agree" , defaultValue = "false") boolean agreement , 
			Model model , HttpSession session)
	{
		
		System.out.println("Inside egister module");
		
		try {
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.jpg");
			
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
//			if(agreement==false)
//			{
//				throw new Exception("Please agree terms and conditions");
//			}
			
			   this.userDao.save(user);
			   
			   System.out.println(user.getEmail());
			   System.out.println(user.getPassword());
			   
			   return "home";
			   
	     	} 
		catch (Exception e) 
		{
			   e.printStackTrace();
			   model.addAttribute(user);
				/*
				 * session.setAttribute("message", new Message("Something went wrong",
				 * "alert-danger"));
				 */			   return "signup";
		}
		
		
		
	}
	
	@RequestMapping("/signin")
	public String login()
	{
		return "login";
	}

	
}

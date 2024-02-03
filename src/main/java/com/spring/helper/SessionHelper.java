package com.spring.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper
{
	
	public void removeSessionAttribute()
	{
		
	try {
          HttpSession session = ( (ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		
		 session.removeAttribute("message");

	}
	catch (Exception e)
	{
	e.printStackTrace();
	}
	
	}

}

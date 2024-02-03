package com.spring.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spring.dao.ContactDao;
import com.spring.dao.UserDao;
import com.spring.entities.Contact;
import com.spring.entities.User;
import com.spring.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController 
{
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ContactDao contactDao;
	
	@ModelAttribute
	public void getUser(Principal p , Model m)
	{
		String email = p.getName();
        System.out.println("Email = "+email);
        
        User user =userDao.findByUsername(email);
    	
		  System.out.println(user.getName());
		  System.out.println(user.getEmail());
		   m.addAttribute( "user" ,user );

 	}
	
	@RequestMapping(path= "/dashboard" , method = RequestMethod.GET)
	public String dashboard(Model m  )
	{
		m.addAttribute("title", "Home");
		System.out.println("Inside USer Controllert");		
				
//		return "user/dashboard";
		return "user/addContact";

	}
	
	@RequestMapping(path="/add-contact" , method = RequestMethod.GET)
	public String addContact(Model m)
	{
		m.addAttribute("title", "Add Contact");
		m.addAttribute("contact", new Contact());
		
		return "user/addContact";
	}
	
	
	@RequestMapping(path="/submitContact" , method = RequestMethod.POST)
	public String submitContact(@ModelAttribute Contact contact  , Principal p,
			@RequestParam("profileImage") MultipartFile receivedFile ,
			HttpSession session
			)
	{
		
		try {
			
//			Setting Image in Database (By just storing its name in database )
		
			if(receivedFile.isEmpty())
			{
//			   DISPLAy ERRROR MESSAGE
				contact.setImage("default.jpg");
			}
			else {
//				STORE THAT FILE
				contact.setImage(receivedFile.getOriginalFilename());
				
			File saveFile = new ClassPathResource("static/img").getFile();     //It will store dnamic address of stored file
			
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+receivedFile.getOriginalFilename());
			
//			1-Input Stream
//			2-PAth (Dynamic Path)
//			3-Option (Rewrite Existing)
			
			Files.copy(receivedFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("File Uploaded");
			}

			String email = p.getName();
			User user = userDao.findByUsername(email);
			
			System.out.println(contact.getName());
			
//			Adding User To Contact (BIDIRECTIONAL MAPPING)
			contact.setUser(user);
//			Adding Contact to User  (BIDIRECTIONAL MAPPIG)
			user.getContacts().add(contact);

			this.userDao.save(user);
			System.out.println("Contact Added Successfully");
			
//			now add SESSION ATTTRIBUTE (Contact Saved Successfully)
			session.setAttribute("message", new Message("Contact Saved Successfully", "primary"));
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
//			now add SESSION ATTRIBUTE  (Something went wrong)
			session.setAttribute("Something Went Wrong", "danger");
		}				
		return "user/addContact";
	}
	
	@RequestMapping(path = "/show-contacts/{page}" , method = RequestMethod.GET)
	public String showContacts(@PathVariable("page") Integer page , Model m , Principal p)
	{
		m.addAttribute("title", "Show Contacts");
		
		String email = p.getName();
		User user = userDao.findByUsername(email);
		
		int userId = user.getId();
		
		
//		We need to create an object of Pageable to pass inside func.
//		We can store object of Page Request inside Pageable as it is parent class
		Pageable pageable = PageRequest.of(page, 2);
		
		 Page<Contact> contacts = contactDao.findContactByUserId(userId , pageable);
		 
//		 We will send 3 things
//		 1 - Contacts list 
//		 2 - CurrentPage
//		 3 - TotalPages
		 
		 m.addAttribute("contacts", contacts);
		 m.addAttribute("currentPage",page);
		 m.addAttribute("totalPages",contacts.getTotalPages());
		 
	    return "user/showContacts";	
	}
	
	@RequestMapping(path="/showIndividual/{contactId}" , method=RequestMethod.GET)
	public String showIndividual(@PathVariable("contactId")Integer contactId  , Model m , Principal p)
	{
		m.addAttribute("contactId", contactId);
		
		System.out.println("This is ShowIdividual Controller");
		
		Contact contact = this.contactDao.findByContactId(contactId);
		
		System.out.println("Contact Name = "+contact.getName());
		
		
	 	String email = p.getName();
	  User user = this.userDao.findByUsername(email);
	  
//	  DOUBLE CHECK
//	  Check whether Jo User Login Hai Vhi Delete Kar rha hai na 
//	  Jo user login hai vo kisi aur ke contacts na access kar paaye
	  if(user.getId() == contact.getUser().getId())
			m.addAttribute("contact", contact);

		  		  
		return "user/showIndividual";
	  
	}
	
	@RequestMapping(path = "/delete-contact/{cid}" , method = RequestMethod.GET)
	public String deleteContact(@PathVariable("cid") Integer cid , Principal p , HttpSession session)
	{
		User user = this.userDao.findByUsername(p.getName());
		Contact contact = this.contactDao.findByContactId(cid);
		
		if(user.getId() == contact.getUser().getId())
		{
			
//			AS user is linked with Contact we cannot Delete it
//			So first free the contact
			System.out.println("Trying to delete Contact");
			
			contact.setUser(null);
			
		this.contactDao.delete(contact);
		session.setAttribute("message", new Message("Contact Deleted Successfully", "primary"));
		}
		return "redirect:/user/show-contacts/0";
	}
	
	@RequestMapping(path = "/edit-contact/{cid}" , method = RequestMethod.GET)
	public String editContact(@PathVariable("cid") Integer cid , Model m)
	{
		Contact contact = this.contactDao.findByContactId(cid);
		
		m.addAttribute("contact",contact);
		return "user/editContact";
	}
	
	@RequestMapping(path="/submitEditContact/{cid}", method = RequestMethod.POST)
	public String submitEditContact(@ModelAttribute Contact newContact , 
			                        @PathVariable("cid")Integer cid ,
			                        @RequestParam("profileImage")MultipartFile receivedFile
			)
	{
		Contact oldContact = this.contactDao.findByContactId(cid);
		
//		UPDATING OLD CONTACT WTH NEW VALUES
		oldContact.setName(newContact.getName());
		oldContact.setEmail(newContact.getEmail());
		oldContact.setPhone(newContact.getPhone());
		oldContact.setSecondname(newContact.getSecondname());
		oldContact.setWork(newContact.getWork());
		oldContact.setDescription(newContact.getDescription());
		
		try {
			
			if(receivedFile.isEmpty())
			{
//				Do NOTHING
			}
			else
			{
				
				
//				DELETE OLD FILE
				File deleteFile = new ClassPathResource("static/img").getFile();     //It will store dnamic address of stored file
				File file1 = new File(deleteFile, oldContact.getImage());
				file1.delete();
				
				
//				STORE NEW FILE
				oldContact.setImage(receivedFile.getOriginalFilename());
			File saveFile = new ClassPathResource("static/img").getFile();     //It will store dnamic address of stored file
			
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+receivedFile.getOriginalFilename());
			Files.copy(receivedFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("File Updated");
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		this.contactDao.save(oldContact);
		
        return "redirect:/user/showIndividual/{cid}";		
	}

}

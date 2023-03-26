package com.nrifintech.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nrifintech.exception.ResourceNotFoundException;
import com.nrifintech.model.User;
import com.nrifintech.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserService userService;
	
	@GetMapping("/showusers")
	public List<User> getAll()
	{
		return userService.getAllUsers();
	}
	
	@PostMapping("/adduser")
	public User create(@RequestBody User user)
	{
		userService.addUser(user);
		return user;
	}
	
	
	@PutMapping("/updateuser/{id}")
	public ResponseEntity<User> updateBook(@PathVariable int id, @RequestBody User user)
			throws ResourceNotFoundException {
		return userService.updateUser(id, user);
	}
	
	@DeleteMapping("/deleteuser/{id}")
	public ResponseEntity<User> deleteUser(@PathVariable int id) throws ResourceNotFoundException {
		return userService.deleteUser(id);
	}
	
	@GetMapping("showuserbyid/{id}")
	public ResponseEntity<User> getUserById(@PathVariable int id) throws ResourceNotFoundException{
		return userService.getUserById(id);
	}
	
	@GetMapping("/showuserbyusername/{username}")
	public ResponseEntity<User> getUserByUsername(@PathVariable String username) throws ResourceNotFoundException
	{
		return userService.getUserByusername(username);
	}
	
	@GetMapping("/showfinebyusername/{username}")
	public ResponseEntity<Integer> getFineByUsername(@PathVariable String username) throws ResourceNotFoundException
	{
		return userService.getFineByUsername(username);
	}
	

	
	@GetMapping("accountrecovery/{username}")
	public ResponseEntity<String> accountRecovery(@PathVariable String username) throws ResourceNotFoundException
	{
		return userService.accountRecovery(username);
	}
	
	@GetMapping("changepassword/{passcode}/{password}")
	public ResponseEntity<String> changepassword(@PathVariable("passcode") String passcode,@PathVariable("password")String password) throws ResourceNotFoundException
	{
		return userService.updatePassword(passcode,password);
	}
}

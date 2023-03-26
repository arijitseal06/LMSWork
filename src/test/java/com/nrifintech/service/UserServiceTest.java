package com.nrifintech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.nrifintech.exception.ResourceNotFoundException;
import com.nrifintech.model.User;
import com.nrifintech.repository.UserRepository;

@SpringBootTest(classes = { UserServiceTest.class })
public class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Test
	@DisplayName("Test Add User")
	public void testAddUser() {
		User user = new User(101, "BhanuPrakash", 21, "bp094201@gmail.com", "ROLE_USER");

		Mockito.when(userRepository.save(user)).thenReturn(user);

		User addedUser = userService.addUser(user);

		assertEquals("bp094201@gmail.com", addedUser.getEmail());
		assertEquals("ROLE_USER", addedUser.getRole());

		Mockito.verify(userRepository, times(1)).save(user);
	}

	@Test
	@DisplayName("Test Get All Users")
	public void testGetAllUsers() {
		User user1 = new User(101, "BhanuPrakash", 21, "bp094201@gmail.com", "ROLE_USER");
		User user2 = new User(102, "Mahesh", 21, "mahesh234@gmail.com", "ROLE_USER");
		User user3 = new User(103, "Devaraj", 21, "dev094201@gmail.com", "ROLE_ADMIN");
		List<User> userList = new ArrayList<User>();
		userList.add(user1);
		userList.add(user2);
		userList.add(user3);

		Mockito.when(userRepository.findAll()).thenReturn(userList);

		List<User> ResultuserList = new ArrayList<User>();

		ResultuserList = userService.getAllUsers();

		assertEquals("dev094201@gmail.com", ResultuserList.get(2).getEmail());
		assertEquals(3, ResultuserList.size());

		Mockito.verify(userRepository, times(1)).findAll();

	}

	@Test
	@DisplayName("Test Update User")
	public void testUpdateUser() throws ResourceNotFoundException {
		User exisitingUser = new User(1001, "Devaraj", 21, "dev094201@gmail.com", "ROLE_ADMIN");
//		exisitingUser.setUsername("devaraj123");
//		exisitingUser.setPassword("Dev@123");
		User user = new User(1001, "Mahesh", 21, "mahesh234@gmail.com", "ROLE_USER");
//		user.setUsername("mahesh123");
//		user.setPassword("mahesh@123");
		Mockito.when(userRepository.findById(1001)).thenReturn(Optional.of(exisitingUser));
		Mockito.when(userRepository.save(exisitingUser)).thenReturn(user);

		User updatedUser = userService.updateUser(1001, user).getBody();

		assertEquals("Mahesh", updatedUser.getName());
		assertEquals("mahesh234@gmail.com", updatedUser.getEmail());

		Mockito.verify(userRepository, times(1)).findById(1001);
		Mockito.verify(userRepository, times(1)).save(exisitingUser);

	}

	@Test
	@DisplayName("Test Delete User")
	public void testDeleteUser() throws ResourceNotFoundException {
		User user = new User(1001, "Mahesh", 21, "mahesh234@gmail.com", "ROLE_USER");

		Mockito.when(userRepository.findById(1001)).thenReturn(Optional.of(user));

		Optional<User> deletedUser = Optional.ofNullable(userService.deleteUser(1001).getBody());

		assertEquals(deletedUser.get().toString(), user.toString());
		assertEquals("Mahesh", deletedUser.get().getName());

		Mockito.verify(userRepository, times(1)).findById(1001);
	}

	@Test
	@DisplayName("Test Get User By Id")
	public void testGetUserById() throws ResourceNotFoundException {
		User user = new User(1001, "Mahesh", 21, "mahesh234@gmail.com", "ROLE_USER");

		Mockito.when(userRepository.findById(1001)).thenReturn(Optional.of(user));
		
		User resultUser = userService.getUserById(1001).getBody();
		
		assertEquals(resultUser.toString(), user.toString());
		
		Mockito.verify(userRepository,times(1)).findById(1001);
	}
	
	@Test
	@DisplayName("Test Get User By User Name")
	public void testGetUserByUserName() throws ResourceNotFoundException {
		User user1 = new User(101, "BhanuPrakash", 21, "bp094201@gmail.com", "ROLE_USER");
		user1.setUsername("BhanuPrakash213");
		User user2 = new User(102, "Mahesh", 21, "mahesh234@gmail.com", "ROLE_USER");
		user2.setUsername("Mahesh123");
		User user3 = new User(103, "Devaraj", 21, "dev094201@gmail.com", "ROLE_ADMIN");
		user3.setUsername("Devaraj12345");
		List<User> userList = new ArrayList<User>();
		userList.add(user1);
		userList.add(user2);
		userList.add(user3);
		
		Mockito.when(userRepository.findAll()).thenReturn(userList);
		
		User resultUser = userService.getUserByusername("BhanuPrakash213").getBody();
		
		assertEquals("BhanuPrakash",resultUser.getName());
		assertEquals("BhanuPrakash213",resultUser.getUsername());
		
		Mockito.verify(userRepository,times(1)).findAll();

	}
	
	@Test
	@DisplayName("Test Update Password")
	public void testUpdatePassword() {
		String passcode = "1234";
		BCryptPasswordEncoder bs=new BCryptPasswordEncoder();
		String encodedPasscode = bs.encode(passcode);
		User user1 = new User(101, "BhanuPrakash", 21, "bp09201@gmail.com", "ROLE_USER");
		user1.setUsername("BhanuPrakash213");
		user1.setPassword(encodedPasscode);
		User user2 = new User(102, "Mahesh", 21, "mahesh234@gmail.com", "ROLE_USER");
		user2.setPassword("Mahesh@123");
		user2.setUsername("Mahesh123");
		User user3 = new User(103, "Devaraj", 21, "dev094201@gmail.com", "ROLE_ADMIN");
		user3.setPassword("Devaraj@123");
		user3.setUsername("Devaraj12345");
		List<User> userList = new ArrayList<User>();
		userList.add(user1);
		userList.add(user2);
		userList.add(user3);
		
		Mockito.when(userRepository.findAll()).thenReturn(userList);
		Mockito.when(userRepository.save(user1)).thenReturn(user1);
		String exceptedresult = "Password Updated";
		
		String result = userService.updatePassword(passcode,"BhanuPrakash213").getBody();
		
		assertEquals(exceptedresult,result);
		
	}

}

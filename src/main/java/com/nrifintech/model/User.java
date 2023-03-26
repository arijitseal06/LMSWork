package com.nrifintech.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userId", nullable = false)
	private int id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "age", nullable = false)
	private int age;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@Column(name = "fine", columnDefinition = "integer default 0")
	private int fine;

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", age=" + age + ", email=" + email + ", username=" + username
				+ ", password=" + password + ", role=" + role + "]";
	}

	@Column(name = "password")
	private String password;

	@Column(name = "role", nullable = false)
	private String role;

	@Column(name = "isEnabled", nullable = false)
	private boolean isEnabled;

//	@OneToMany(mappedBy = "user",fetch=FetchType.LAZY)
//	@JsonBackReference
//	private List<Issue> issues = new ArrayList<>();
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "fileId")
	private DatabaseFile databaseFile;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getFine() {
		return fine;
	}

	public void setFine(int fine) {
		this.fine = fine;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public DatabaseFile getDatabaseFile() {
		return databaseFile;
	}

	public void setDatabaseFile(DatabaseFile databaseFile) {
		this.databaseFile = databaseFile;
	}

	

	public User(int id, String name, int age, String email, String username, int fine, String password, String role,
			boolean isEnabled, DatabaseFile databaseFile) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.email = email;
		this.username = username;
		this.fine = fine;
		this.password = password;
		this.role = role;
		this.isEnabled = isEnabled;
		this.databaseFile = databaseFile;
	}

	public User(String name, int age, String email, String username, String password, String role) {
		super();
		this.name = name;
		this.age = age;
		this.email = email;
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public User(int id, String name, int age, String email, String role) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.email = email;
		this.role = role;
	}

	public User(String username) {
		this.username = username;
	}

	public User() {
		super();
	}

	public boolean hasRole(String roleName) {
		if (role.equals(roleName))
			return true;
		return false;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

}

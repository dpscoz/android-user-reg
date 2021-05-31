package com.user_register.UserRegister;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity(name = "app_user")
public class AppUser {
	private Integer id;
	private String name;
	
	@JsonFormat
	  (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate birthday;
	private String picture;

	public AppUser() {
	}

	public AppUser(Integer id, String name, LocalDate birthday, String picture) {
		this.id = id;
		this.name = name;
		this.birthday = birthday;
		this.picture = picture;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

}

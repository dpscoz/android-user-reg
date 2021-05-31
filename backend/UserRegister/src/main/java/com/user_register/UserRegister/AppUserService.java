package com.user_register.UserRegister;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {
	
	@Autowired
	private AppUserRepository rep;
	
	public List<AppUser> listAll() {
		return rep.findAll();
	}
	
	public void save(AppUser appUser) {
		rep.save(appUser);
	}
	
	public AppUser get(Integer id) {
		return rep.findById(id).get();
	}
	
	public void delete(Integer id) {
		rep.deleteById(id);
	}
	
	
}

package com.user_register.UserRegister;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppUserController {
	
	@Autowired
	private AppUserService service;
	
	@GetMapping("/users")
	public List<AppUser> list() {
		List<AppUser> list = service.listAll();
		System.out.println("\n\nIRIRIRIRIRIRIRRIRRRIIRIRIRIIRIRIRIIR LIST USER: " + list.size());
		return list;
	}
	
	@GetMapping("/users/{id}")
	public ResponseEntity<AppUser> get(@PathVariable Integer id) throws ParseException {
		try {
			
			AppUser appUser = service.get(id);
			
			System.out.println("\n\nIRIRIRIRIRIRIRRIRRRIIRIRIRIIRIRIRIIR LIST USER: " + appUser.getBirthday());
			//String dataEmUmFormato = appUser.getBirthday().toString(); 
			//SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd"); 
			//Date data = formato.parse(dataEmUmFormato); 
			//formato.applyPattern("dd/MM/yyyy");
			
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			String dataFormatada = formato.format(appUser.getBirthday());
			System.out.println(dataFormatada);
			
			return new ResponseEntity<AppUser>(appUser, HttpStatus.OK);
		} catch (NoSuchElementException ex) {
			return new ResponseEntity<AppUser>(HttpStatus.NOT_FOUND); 
		}		
	}
	
	@PostMapping("/users")
	public void add(@RequestBody AppUser appUser){
		System.out.println("\n\nIRIRIRIRIRIRIRRIRRRIIRIRIRIIRIRIRIIR USER: " + appUser.getName() + " bday: " + appUser.getBirthday());
		service.save(appUser);
	}
	
	@PutMapping("/users/{id}")
	public ResponseEntity<AppUser> update(@RequestBody AppUser appUser, @PathVariable Integer id){
		try {
			
			AppUser exist = service.get(id);
			System.out.println("\n\nIRIRIRIRIRIRIRRIRRRIIRIRIRIIRIRIRIIR update USER: " + appUser.getBirthday());
			service.save(appUser);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (NoSuchElementException ex) {
			return new ResponseEntity<AppUser>(HttpStatus.NOT_FOUND); 
		}
				
	}
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<AppUser> delete( @PathVariable Integer id){
		System.out.println("\n\nIRIRIRIRIRIRIRRIRRRIIRIRIRIIRIRIRIIR delete: " + id);
		try {
			AppUser exist = service.get(id);
			service.delete(id);
			return new ResponseEntity<>(HttpStatus.OK);	
		} catch (NoSuchElementException ex) {
			return new ResponseEntity<AppUser>(HttpStatus.NOT_FOUND); 
		}
		
	}
	
}

package com.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.config.JwtProvider;
import com.app.entities.User;
import com.app.exception.UserException;
import com.app.repository.UserRepository;
import com.app.request.LoginRequest;
import com.app.responce.AuthResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private UserRepository userRepositroy;
	private JwtProvider jwtProvider;
	private PasswordEncoder passwordEncoder;
	
	public AuthController(UserRepository userRepositroy) {
		this.userRepositroy=userRepositroy;
	}
	
	@PostMapping("/SignUp")
	public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException{
		String email=user.getEmail();
		String password=user.getPassword();
		String firstName=user.getFirstName();
		String LastName=user.getLastName();
		
		User isEmailExist=userRepositroy.findByEmail(email);
		
		if(isEmailExist!=null) {
			throw new UserException("Email is already used with another account");
		}
		User createdUser=new User();
		createdUser.setEmail(email);
		createdUser.setPassword(passwordEncoder.encode(password));
		createdUser.setFirstName(firstName);
		createdUser.setLastName(LastName);
		User savedUser=userRepositroy.save(createdUser);
		
		Authentication authentication=new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String token=jwtProvider.generateToken(authentication);
		
		AuthResponse authResponce=new AuthResponse(token, true); 
		
		return new ResponseEntity<AuthResponse>(authResponce, HttpStatus.CREATED);
	}
	
	public ResponseEntity<AuthResponse> loginUserHandler(@RequestBody LoginRequest loginRequest){
		
		return null;
	}
	
	

}

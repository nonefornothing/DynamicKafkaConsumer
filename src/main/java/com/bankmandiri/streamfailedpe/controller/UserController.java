package com.bankmandiri.streamfailedpe.controller;

import com.bankmandiri.streamfailedpe.model.User;
import com.bankmandiri.streamfailedpe.model.UserResponses;
import com.bankmandiri.streamfailedpe.exception.CustomException;
import com.bankmandiri.streamfailedpe.services.UserService;
import com.bankmandiri.streamfailedpe.utils.ErrorCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping("/signin")
	public ResponseEntity login(@RequestBody User user) {
		UserResponses s= new UserResponses();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		String token =null;
		User usr = new User();
		try {
			if (user.getUsername().isEmpty()) {
				s.setResponses_code(ErrorCodeEnum.USERNAME_NOT_VALID.getCode());
				s.setResponse_message(ErrorCodeEnum.USERNAME_NOT_VALID.getDefaultMsg());
				s.setResponse_timestamp(sdf.format(new Date()));
			}
			if (user.getPassword().isEmpty()) {
				s.setResponses_code(ErrorCodeEnum.PASS_NOT_VALID.getCode());
				s.setResponse_message(ErrorCodeEnum.PASS_NOT_VALID.getDefaultMsg());
				s.setResponse_timestamp(sdf.format(new Date()));
			}
			
			token = userService.signin(user.getUsername(), user.getPassword());
			usr = userService.getUserByUsrName(user.getUsername());
			usr.setToken("Bearer "+token);
			s.setResponses_code(ErrorCodeEnum.SUCCESS.getCode());
			s.setResponse_message(usr);
			s.setResponse_timestamp(sdf.format(new Date()));
		} catch (CustomException e) {
			s.setResponses_code(ErrorCodeEnum.SIGNIN_FAILED.getCode());
			s.setResponse_message(e.getMessage());
			s.setResponse_timestamp(sdf.format(new Date()));
		}catch (Exception e) {
			s.setResponses_code(ErrorCodeEnum.UNKNOWN_ERROR.getCode());
			s.setResponse_message(ErrorCodeEnum.UNKNOWN_ERROR.getDefaultMsg());
			s.setResponse_timestamp(sdf.format(new Date()));
		}
		return new ResponseEntity(s, HttpStatus.OK);
	}

	@PostMapping("/signup")
	public String signup(@RequestBody User user) {
		return userService.signup(user);
	}

	@DeleteMapping(value = "/{username}")
	public String delete(@PathVariable String username) {
		userService.deleteByUsrName(username);
		return username;
	}

	@GetMapping(value = "/me")
	public User whoami(HttpServletRequest req) {
		return userService.whoami(req);
	}

	@GetMapping("/refresh")
	public String refresh(HttpServletRequest req) {
		return userService.refresh(req.getRemoteUser());
	}

}

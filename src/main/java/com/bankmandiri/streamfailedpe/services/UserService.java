package com.bankmandiri.streamfailedpe.services;

import com.bankmandiri.streamfailedpe.model.User;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

	void create(User usr);

	User getUserByUsrName(String usr);

	void deleteByUsrName(String usr);

	String signin(String username, String password);

	String signup(User user);

	User whoami(HttpServletRequest req);

	String refresh(String username);
}

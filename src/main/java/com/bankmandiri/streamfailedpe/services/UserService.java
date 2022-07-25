package com.bankmandiri.streamfailedpe.services;

import com.bankmandiri.streamfailedpe.model.User;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

	public String create(User usr);

	public User getUserByUsrName(String usr);

	public String deleteByUsrName(String usr);

	public String signin(String username, String password);

	public String signup(User user);

	public User whoami(HttpServletRequest req);

	public String refresh(String username);
}

package com.bankmandiri.streamfailedpe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	private String username;
	private String password;
	private String token;
	List<Role> roles;

}

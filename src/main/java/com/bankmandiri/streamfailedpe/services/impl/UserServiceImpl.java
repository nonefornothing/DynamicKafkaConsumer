package com.bankmandiri.streamfailedpe.services.impl;

import com.bankmandiri.streamfailedpe.exception.CustomException;
import com.bankmandiri.streamfailedpe.model.User;
import com.bankmandiri.streamfailedpe.security.JwtTokenProvider;
import com.bankmandiri.streamfailedpe.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.bankmandiri.streamfailedpe.utils.Constant.USER_INDEX;
import static com.bankmandiri.streamfailedpe.utils.Constant.USER_TYPE;

@Service
public class UserServiceImpl implements UserService {

	private final RestHighLevelClient client;

	private final ObjectMapper objectMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	public UserServiceImpl(RestHighLevelClient client, ObjectMapper objectMapper) {
		this.client = client;
		this.objectMapper = objectMapper;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> convertToMap(User data) {
		return objectMapper.convertValue(data, Map.class);
	}

	private User convertFromMap(Map<String, Object> map) {
		return objectMapper.convertValue(map, User.class);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void create(User usr) {
		try {
			IndexRequest indexRequest = new IndexRequest(USER_INDEX, USER_TYPE, usr.getUsername()).source(convertToMap(usr),
					XContentType.JSON);
			IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
			indexResponse.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public User getUserByUsrName(String usr) {
		try {
			GetRequest getRequest = new GetRequest(USER_INDEX,USER_TYPE, usr);
			GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
			Map<String, Object> resultMap = getResponse.getSource();
			User user = convertFromMap(resultMap);
			if (user == null) {
				throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
			}
			return user;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void deleteByUsrName(String usr) {
		try {
			DeleteRequest deleteRequest = new DeleteRequest(USER_INDEX, usr);
			DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
			response.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String signin(String username, String password) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			return jwtTokenProvider.createToken(username, getUserByUsrName(username).getRoles());
		} catch (AuthenticationException e) {
			throw new CustomException("Invalid username/password", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@Override
	public String signup(User user) {
		User usr = getUserByUsrName(user.getUsername());
		if (usr == null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			create(user);
			return jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
		} else {
			throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@Override
	public User whoami(HttpServletRequest req) {
		return getUserByUsrName(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
	}

	@Override
	public String refresh(String username) {
		return jwtTokenProvider.createToken(username, getUserByUsrName(username).getRoles());
	}

}

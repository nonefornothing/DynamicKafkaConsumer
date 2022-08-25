package com.bankmandiri.streamfailedpe.utils;

import com.bankmandiri.streamfailedpe.model.Role;
import com.bankmandiri.streamfailedpe.model.User;
import com.bankmandiri.streamfailedpe.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.ArrayList;
import java.util.List;

@Configuration
public class InitUser {

    private final Logger logger = LoggerFactory.getLogger(InitUser.class);

    @Value("${root.user}")
    private String rootUser;

    @Value("${root.password}")
    private String rootPassword;

    @Bean
    CommandLineRunner initUserRoot(UserService userService){
        return args -> {
          System.out.println("Hello");
          List<Role> roles = new ArrayList<>();
          roles.add(Role.ADMIN);
          roles.add(Role.CLIENT);
          System.out.println(roles);
          if (userService.getUserByUsrName(rootUser) == null) {
              userService.create(new User(rootUser,
                      rootPassword,
                      "",
                      roles));
          } else {
              logger.info("User already in elasticSearch");
          }
        };
    }

}


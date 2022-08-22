package com.bankmandiri.streamfailedpe.security;

import com.bankmandiri.streamfailedpe.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${root.user}")
  private String rootUser;

  @Value("${root.password}")
  private String rootPassword;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

//  @Override
//  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//    auth.inMemoryAuthentication()
//            .withUser(rootUser)
//            .password(rootPassword)
//            .roles(Role.ADMIN.toString())
//            .and()
//            .withUser("Zack")
//            .password("aayush")
//            .roles("admin_role")
//            .and()
//            .withUser("GFG")
//            .password("Helloword")
//            .roles("student");
//  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // Disable CSRF (cross site request forgery)
    http.csrf().disable();

    // No session will be created or used by spring security
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // Entry points
    http.authorizeRequests()//
            .antMatchers("/users/signin").permitAll()//
            .antMatchers("/users/signup").hasRole(Role.ADMIN.toString())
            .antMatchers("/delete").hasRole(Role.ADMIN.toString())
            .antMatchers("/delete").hasRole("admin_role")
            .antMatchers("/details").hasAnyRole("admin_role","student")

//        .antMatchers("/users/signup").hasRole("ADMIN")
//        .antMatchers("/users/delete").hasRole("ADMIN")

        .antMatchers("**/consumer-management").permitAll()


            // Disallow everything else..
        .anyRequest().authenticated();

    // If a user try to access a resource without having enough permissions
//    http.exceptionHandling().accessDeniedPage("/login");

    // Apply JWT
    http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));

    // Optional, if you want to test the API from a browser
    // http.httpBasic();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

//  @Bean
//  public PasswordEncoder getPasswordEncoder(){
//    return NoOpPasswordEncoder.getInstance();
//  }

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

}

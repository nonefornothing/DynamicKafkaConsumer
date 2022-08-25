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
//            .withUser("ADMIN")
//            .password(passwordEncoder().encode("Helloword"))
//            .roles("ADMIN")
//            .and()
//            .withUser("CLIENT")
//            .password(passwordEncoder().encode("Helloword"))
//            .roles("CLIENT");
//  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

//    http.csrf().disable()
//        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        .and()
//        .authorizeRequests()
//            .antMatchers("/delete").hasRole("ADMIN")
//            .antMatchers("/details").hasAnyRole("ADMIN","CLIENT")
//            .antMatchers("/stream-failed-pe/users/signup").hasRole("ADMIN")
//        .and()
//        .httpBasic()
//        .and()
//        .authorizeRequests()
//            .antMatchers("/stream-failed-pe/users/signin").permitAll()
//            .antMatchers("/stream-failed-pe/consumer-management/**").hasAnyRole("ADMIN","CLIENT")
//            .antMatchers("/stream-failed-pe/pe-management/**").hasAnyRole("ADMIN","CLIENT")
//            .anyRequest().authenticated()
//        .and()
//        .apply(new JwtTokenFilterConfigurer(jwtTokenProvider));

    // Disable CSRF (cross site request forgery)
    http.csrf().disable();

    // No session will be created or used by spring security
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.authorizeRequests()
        .antMatchers("/delete").hasRole("ADMIN")
        .antMatchers("/details").hasAnyRole("ADMIN","CLIENT")
        .antMatchers("/stream-failed-pe/users/signup").hasRole("ADMIN")
        .antMatchers("/stream-failed-pe/users/signin").permitAll()

            // Entry points
//    http.authorizeRequests()//
//        .antMatchers("/stream-failed-pe/users/signin").permitAll()//
//        .antMatchers("/stream-failed-pe/consumer-management/").hasAnyRole("ADMIN","CLIENT")
//        .antMatchers("/stream-failed-pe/pe-management/").hasAnyRole("ADMIN","CLIENT")

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

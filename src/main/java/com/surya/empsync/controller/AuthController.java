package com.surya.empsync.controller;

import com.surya.empsync.model.AppRole;
import com.surya.empsync.model.Role;
import com.surya.empsync.model.User;
import com.surya.empsync.repository.RoleRepository;
import com.surya.empsync.repository.UserRepository;
import com.surya.empsync.security.jwt.JwtUtils;
import com.surya.empsync.security.request.LoginRequest;
import com.surya.empsync.security.request.SignupRequest;
import com.surya.empsync.security.response.MessageResponse;
import com.surya.empsync.security.response.UserInfoResponse;
import com.surya.empsync.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                            loginRequest.getPassword())
            );
        }catch (AuthenticationException e){
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        UserInfoResponse loginResponse = new UserInfoResponse(userDetails.getId(), jwtToken,
                userDetails.getUsername(), roles);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if(userRepository.existsByUserName(signupRequest.getUserName())){
            return ResponseEntity.badRequest().body(
                    new MessageResponse("Error: Username is already taken!"));
        }
        if(userRepository.existsByEmailId(signupRequest.getEmailId())){
            return ResponseEntity.badRequest().body(
                    new MessageResponse("Error: Username is already taken!"));
        }
        User user = new User(signupRequest.getUserName(), signupRequest.getEmailId(),
                passwordEncoder.encode(signupRequest.getPassword())
        );
        Set<String> roleString = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if(roleString == null){
            Role role = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(() ->
                    new RuntimeException("Error: Role is not found")
            );
            roles.add(role);
        }else{
            roleString.forEach(role ->{
                Role userRole;
                if (role.equals("admin")) {
                    userRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(() ->
                            new RuntimeException("Error: Role is not found"));
                } else {
                    userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(() ->
                            new RuntimeException("Error: Role is not found"));
                }
                roles.add(userRole);
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok().body(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/username")
    public ResponseEntity<String> currentUserName(Authentication authentication){
        String username = "";
        if (authentication != null) {
            username = authentication.getName();
        }
        return new ResponseEntity<>(username, HttpStatus.OK);
    }
}

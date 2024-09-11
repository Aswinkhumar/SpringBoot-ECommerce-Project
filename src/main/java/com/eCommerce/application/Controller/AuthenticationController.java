package com.eCommerce.application.Controller;

import com.eCommerce.application.Entity.AppRoles;
import com.eCommerce.application.Entity.Roles;
import com.eCommerce.application.Entity.User;
import com.eCommerce.application.Repository.RolesRepository;
import com.eCommerce.application.Repository.UserRepository;
import com.eCommerce.application.Security.JWT.JWTUtils;
import com.eCommerce.application.Security.Model.LoginRequest;
import com.eCommerce.application.Security.Model.LoginResponse;
import com.eCommerce.application.Security.Model.MessageResponse;
import com.eCommerce.application.Security.Model.SignupRequest;
import com.eCommerce.application.Security.Service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie responseCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        LoginResponse response = new LoginResponse(userDetails.getUserId() , userDetails.getUsername(), roles);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){

        if(userRepository.existsByUserName(signupRequest.getUserName())){
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error while trying to create user with username:"+signupRequest.getUserName()));
        }

        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error while trying to create user with Email: "+signupRequest.getEmail()));
        }

        User user = new User(signupRequest.getUserName(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));

        Set<String> rolesInString = signupRequest.getRole();
        Set<Roles> roles = new HashSet<>();

        if(rolesInString == null){
            Roles role = rolesRepository.findByRoleName(AppRoles.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("ERROR: Role is not found"));
            roles.add(role);
        }
        else {
            rolesInString.forEach(role -> {
                switch (role){
                    case "admin":
                        Roles adminRole = rolesRepository.findByRoleName(AppRoles.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("ERROR: Role is not found"));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Roles sellerRole = rolesRepository.findByRoleName(AppRoles.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("ERROR: Role is not found"));
                        roles.add(sellerRole);
                        break;
                    default:
                        Roles userRole = rolesRepository.findByRoleName(AppRoles.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("ERROR: Role is not found"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User Registered Successfully!"));
    }

    @GetMapping("/username")
    public String getCurrentUserName(Authentication authentication){
        if(authentication !=  null){
            return authentication.getName();
        }
        else {
            return "NULL";
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication){

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        LoginResponse response = new LoginResponse(userDetails.getUserId() , userDetails.getUsername(), roles);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser(){
        ResponseCookie responseCookie = jwtUtils.generateCleanCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new MessageResponse("You have been Signed out successfully!"));
    }
}

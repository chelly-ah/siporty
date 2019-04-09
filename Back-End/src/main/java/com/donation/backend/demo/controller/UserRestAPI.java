package com.donation.backend.demo.controller;

import com.donation.backend.demo.message.request.SignUpForm;
import com.donation.backend.demo.message.request.UserInfo;
import com.donation.backend.demo.message.response.ResponseMessage;
import com.donation.backend.demo.model.Role;
import com.donation.backend.demo.model.RoleName;
import com.donation.backend.demo.model.User;
import com.donation.backend.demo.repository.RoleRepository;
import com.donation.backend.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserRestAPI {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserInfo>> allUsers() {

        try {
            List<UserInfo> _users = new ArrayList<>();
            List<User> users = new ArrayList<>(userRepository.findAll());
            if(users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            users.forEach(user -> {
                UserInfo _user = new UserInfo();
                _user.setId(user.getId());
                _user.setFirstname(user.getFirstName());
                _user.setLastname(user.getLastName());
                _user.setUsername(user.getUsername());
                _user.setEmail(user.getEmail());
                _user.setEnabled(user.isEnabled());

                List<String> _roles = new ArrayList<>();
                Set<Role> roles = user.getRoles();
                roles.forEach(role -> {
                    _roles.add(role.getName().name());
                });

                _user.setRoles(_roles);
                _users.add(_user);
            });

            return new ResponseEntity<>(_users, HttpStatus.OK);

        } catch(Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfo> getUserById(@PathVariable("id") long id) {

        Optional<User> userOptional = userRepository.findById(id);

        if(userOptional.isPresent()) {

            User user = userOptional.get();
            UserInfo _user = new UserInfo();
            _user.setId(user.getId());
            _user.setFirstname(user.getFirstName());
            _user.setLastname(user.getLastName());
            _user.setUsername(user.getUsername());
            _user.setEmail(user.getEmail());
            _user.setEnabled(user.isEnabled());

            List<String> _roles = new ArrayList<>();
            Set<Role> roles = user.getRoles();
            roles.forEach(role -> {
                _roles.add(role.getName().name());
            });

            _user.setRoles(_roles);

            return new ResponseEntity<>(_user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> newGame(@RequestBody SignUpForm signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Email is already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(signUpRequest.getFirstname(), signUpRequest.getLastname(), signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), true);

        //Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        /*strRoles.forEach(role -> {
            Role roleDb = roleRepository.findByName(RoleName.valueOf(role))
                    .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Role not found."));
            roles.add(roleDb);
        });*/

        Role roleDb = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Role not found."));
        roles.add(roleDb);

        user.setRoles(roles);
        userRepository.save(user);

        return new ResponseEntity<>(new ResponseMessage("User registered successfully!"), HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity<UserInfo> updateUser(@RequestBody UserInfo user) {

        Optional<User> userOptional = userRepository.findById(user.getId());
        if(userOptional.isPresent()) {
            User _user = userOptional.get();

            _user.setFirstName(user.getFirstname());
            _user.setLastName(user.getLastname());
            _user.setUsername(user.getUsername());
            _user.setEmail(user.getEmail());
            _user.setEnabled(user.isEnabled());
            //_user.setPassword(encoder.encode(user.getPassword());

            userRepository.save(_user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> banUserInfoUserById(@PathVariable("id") Long id) {

        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()) {
            User _user = userOptional.get();
            _user.setEnabled(false);
            userRepository.save(_user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
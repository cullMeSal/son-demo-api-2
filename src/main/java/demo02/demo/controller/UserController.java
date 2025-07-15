package demo02.demo.controller;

import demo02.demo.dto.UserAuthDTO;
import demo02.demo.dto.UserCreationDTO;
import demo02.demo.entity.User;
import demo02.demo.repository.UserRepository;
import demo02.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserCreationDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserAuthDTO user){
        Optional<User> foundUser = userRepo.findByUsername(user.getUsername());
        if (foundUser.isPresent()) {
            if (user.getPassword().equals(foundUser.get().getPassword())){
            return ResponseEntity.ok("Login successfully");
        }}
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials");
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id){
        Optional<User> foundUser = userRepo.findById(id);
        if (foundUser.isPresent()) return ResponseEntity.ok(foundUser.get());
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

}

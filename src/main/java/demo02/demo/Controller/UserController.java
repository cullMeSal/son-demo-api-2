package demo02.demo.Controller;

import demo02.demo.Entity.User;
import demo02.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserRepository userRepo;

    @GetMapping("/sayhi")
    public ResponseEntity<String> sayhi(){
        return ResponseEntity.ok("Henlo");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepo.findByUsername(user.getUsername()).isEmpty()) return ResponseEntity.ok(userRepo.save(user));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username already exist.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user){
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

package demo02.demo.Controller;

import demo02.demo.Entity.User;
import demo02.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id){
        User foundUser = userRepo.getById(id);
        return ResponseEntity.ok(foundUser);
    }

}

package demo02.demo.service;

import demo02.demo.dto.UserCreationDTO;
import demo02.demo.entity.User;
import demo02.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    public ResponseEntity<?> createUser(UserCreationDTO userDTO){
        if (userRepo.findByUsername(userDTO.getUsername()).isEmpty()
                && userDTO.getEmail().matches("^[\\w_+-.]+@[\\w_+-.]+\\.[a-zA-Z]{2,}$")) {

            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setEmail(userDTO.getEmail());

            return ResponseEntity.ok(userRepo.save(user));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username already exist.");
    }
}

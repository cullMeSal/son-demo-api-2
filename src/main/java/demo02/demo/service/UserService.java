package demo02.demo.service;

import demo02.demo.dto.UserAuthDTO;
import demo02.demo.dto.UserCreationDTO;
import demo02.demo.entity.UserEntity;
import demo02.demo.exception.UsernameExistException;
import demo02.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

//    @Autowired
//    private PasswordEncoder passwordEncoder;


    public ResponseEntity<?> registerNewUserAccount(UserCreationDTO userDTO)  {
        if (userRepo.findByUsername(userDTO.getUsername()).isEmpty()
                && userDTO.getEmail().matches("^[\\w_+-.]+@[\\w_+-.]+\\.[a-zA-Z]{2,}$")) {
            UserEntity user = new UserEntity();
            user.setUsername(userDTO.getUsername());
            user.setPassword(new BCryptPasswordEncoder(4).encode(userDTO.getPassword()));
            user.setEmail(userDTO.getEmail());

            return ResponseEntity.ok(userRepo.save(user));
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username already exist or invalid email format.");
    }

    // consider replacing this method
    public ResponseEntity<?> createUser(UserCreationDTO userDTO){
        if (userRepo.findByUsername(userDTO.getUsername()).isEmpty()
                && userDTO.getEmail().matches("^[\\w_+-.]+@[\\w_+-.]+\\.[a-zA-Z]{2,}$")) {

            UserEntity user = new UserEntity();
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setEmail(userDTO.getEmail());

            return ResponseEntity.ok(userRepo.save(user));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username already exist.");
    }


}

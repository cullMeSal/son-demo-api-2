package demo02.demo.controller;

import demo02.demo.dto.UserAuthDTO;
import demo02.demo.dto.UserCreationDTO;
import demo02.demo.entity.UserEntity;
import demo02.demo.exception.UsernameExistException;
import demo02.demo.jwtutils.JwtUserDetailsService;
import demo02.demo.jwtutils.TokenManager;
import demo02.demo.jwtutils.models.JwtRequestModel;
import demo02.demo.jwtutils.models.JwtResponseModel;
import demo02.demo.repository.UserRepository;
import demo02.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/sayhi")
    public ResponseEntity<String> sayhi(){
        System.out.println("helo");
        return ResponseEntity.ok("henlo user");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserCreationDTO userDTO) throws UsernameExistException {
        return userService.registerNewUserAccount(userDTO);
    }

    // Try to make sense of this mapping here.
    @PostMapping("/login")
    public ResponseEntity<JwtResponseModel> createToken(@RequestBody JwtRequestModel request) throws Exception{
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e){
            throw new Exception("INVALID_CREDENTIALS", e);
        }
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(request.getUsername());
        final String jwtToken = tokenManager.generateJwtToken(userDetails);
        return ResponseEntity.ok(new JwtResponseModel(jwtToken));

    }

    @PostMapping("/loginold")
    public ResponseEntity<String> login(@RequestBody UserAuthDTO user){
        Optional<UserEntity> foundUser = userRepo.findByUsername(user.getUsername());
        if (foundUser.isPresent()) {
            // use password encoder to compare raw user entered password with encoded password
            if (passwordEncoder.matches(user.getPassword(),foundUser.get().getPassword())){
            return ResponseEntity.ok("Login successfully");
        }}
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials");
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id){
        Optional<UserEntity> foundUser = userRepo.findById(id);
        if (foundUser.isPresent()) return ResponseEntity.ok(foundUser.get());
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

}

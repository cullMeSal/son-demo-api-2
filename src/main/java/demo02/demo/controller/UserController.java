package demo02.demo.controller;

import demo02.demo.dto.UserAuthDTO;
import demo02.demo.dto.UserCreationDTO;
import demo02.demo.dto.UserQueryRequestModel;
import demo02.demo.dto.UserQueryResultDTO;
import demo02.demo.entity.UserEntity;
import demo02.demo.jwtutils.models.JwtRequestModel;
import demo02.demo.jwtutils.models.JwtResponseModel;
import demo02.demo.repository.UserRepository;
import demo02.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userService;

//    @Autowired
//    private PasswordEncoder passwordEncoder;


    @GetMapping("/sayhi")
    public ResponseEntity<String> sayhi(){
        System.out.println("helo");
        return ResponseEntity.ok("henlo user");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserCreationDTO userDTO)  {
        return userService.registerNewUserAccount(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseModel> createToken(@RequestBody JwtRequestModel request) throws Exception{
        return userService.authenticateLogin(request);

    }

    // Old login mapping that'll not encrypt password on register. Awaiting removal
    @PostMapping("/loginold")
    public ResponseEntity<String> login(@RequestBody UserAuthDTO user){
        Optional<UserEntity> foundUser = userRepo.findByUsername(user.getUsername());
        if (foundUser.isPresent()) {
            // use password encoder to compare raw user entered password with encoded password
            if (new BCryptPasswordEncoder(4).matches(user.getPassword(),foundUser.get().getPassword())){
            return ResponseEntity.ok("Login successfully");
        }}
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials");
    }

    @PostMapping("/query")
    public ResponseEntity<?> queryUser(
            @RequestBody UserQueryRequestModel request,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "page", required = false) Integer page
    )
    {
        List<UserQueryResultDTO> userList = userService.userQuery(limit, page ,request.getUsername(), request.getEmail());
        return ResponseEntity.ok(userList);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id, Authentication authentication){
        return userService.getUserInfo(id, authentication);
    }


}

package demo02.demo.service;

import demo02.demo.dto.UserCreationDTO;
import demo02.demo.dto.UserQueryRequestModel;
import demo02.demo.dto.UserQueryResultDTO;
import demo02.demo.entity.UserEntity;
import demo02.demo.exception.*;
import demo02.demo.jwtutils.JwtUserDetailsService;
import demo02.demo.jwtutils.TokenManager;
import demo02.demo.jwtutils.models.JwtRequestModel;
import demo02.demo.jwtutils.models.JwtResponseModel;
import demo02.demo.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private TokenManager tokenManager;

    @PersistenceContext
    EntityManager entityManager;

//    @Autowired
//    private PasswordEncoder passwordEncoder;


    public ResponseEntity<?> registerNewUserAccount(UserCreationDTO userDTO) {
        if (userRepo.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistException("Username already exist");
        }
        if (!userDTO.getEmail().matches("^[\\w_+-.]+@[\\w_+-.]+\\.[a-zA-Z]{2,}$")){
            throw new InvalidEmailFormatException("Invalid email format");
        }
        UserEntity user = new UserEntity();
        user.setUsername(userDTO.getUsername());
        user.setPassword(new BCryptPasswordEncoder(4).encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());

        return ResponseEntity.ok(userRepo.save(user));
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

    public ResponseEntity<JwtResponseModel> authenticateLogin(JwtRequestModel request) throws RuntimeException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            System.out.println("Login: LLLLLL");
        } catch (DisabledException e) {
            throw new RuntimeException("USER_DISABLED", e);
        } catch (BadCredentialsException e){
            throw new RuntimeException("INVALID_CREDENTIALS", e);
        }
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(request.getUsername());
        final String jwtToken = tokenManager.generateJwtToken(userDetails);
        return ResponseEntity.ok(new JwtResponseModel(jwtToken));
    }

    public ResponseEntity<?> getUserInfo(Long id, Authentication authentication){
        System.out.println("Authentication: "+ authentication.getName());

        Optional<UserEntity> requestingUser = userRepo.findByUsername(authentication.getName());

//        Optional<UserEntity> foundUser = userRepo.findById(id);
        if (!(requestingUser.get().getId() == id)){
            throw new DeniedUserInfoRequestException("Error getting user info: User not found or You are unauthorized to get info of user with id: "+id);
        }
        return ResponseEntity.ok(requestingUser.get());
    }
    public List<UserQueryResultDTO> userQuery(Integer limit, Integer page, String username, String email){
        // Setup query
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserQueryResultDTO> cq = cb.createQuery(UserQueryResultDTO.class);

        Root<UserEntity> user = cq.from(UserEntity.class);
        cq.select(cb.construct(
                UserQueryResultDTO.class,
                user.get("id"),
                user.get("username"),
                user.get("email")
        ));
        List<Predicate> predicates = new ArrayList<>();

        if (username != null) {predicates.add(cb.like(user.get("username"), "%" + username + "%"));}
        if (email != null) {predicates.add(cb.like(user.get("email"), "%" + email + "%"));}
        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<UserQueryResultDTO> query = entityManager.createQuery(cq);

        List<UserQueryResultDTO> resultList = query.getResultList();


        // Pagination
        Integer size = resultList.size();
        // (limit == null && page == null) || (limit != null && page != null) #reserved

        if (limit == null && page != null) throw new InvalidUserQueryRequestException(
                "Invalid Query Request: 'page' param can't be used without 'limit' param.");
        if (limit != null && page == null) return query.getResultList().subList(0, limit);
        if (limit != null && page != null) {
            if (limit <= 0 || page <= 0) throw new NonPositiveInputException("Page limit and Page number cannot be non-positive.");
            if ((page - 1) * limit >= size) {
                throw new UserQueryOutOfBoundException("Queried list is beyond result list size: " + size);
            }
            Integer start = (page -1) * limit;
            Integer end = (page * limit > size) ? size : page * limit;

            return query.getResultList().subList(start, end);
        }

        return query.getResultList();
    }


}

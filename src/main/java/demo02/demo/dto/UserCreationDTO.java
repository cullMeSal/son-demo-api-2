package demo02.demo.dto;

import lombok.Data;

@Data
public class UserCreationDTO {
    private String username;
    private String password;
    private String email;
}

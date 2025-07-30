package demo02.demo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserQueryResultDTO implements Serializable {
    private static final long serialVersionUID = 124L;
    private Long id;
    private String username;
    private String email;

    public UserQueryResultDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}

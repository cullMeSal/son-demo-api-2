package demo02.demo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserQueryRequestModel implements Serializable {
    private static final long serialVersionUID = 124L;
    private String username;
    private String email;

    public UserQueryRequestModel(String username, String email) {
        this.username = username;
        this.email = email;
    }
}

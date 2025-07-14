package demo02.demo.Entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jdk.jfr.Name;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Nonnull
    @Column(name = "user_id")
    private Long id;

    @Nonnull
    @Column(unique = true, name = "user_name")
    private String username;

    @Nonnull
    @Column(name = "user_password")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Nonnull
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nonnull String username) {
        this.username = username;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nonnull String password) {
        this.password = password;
    }
}

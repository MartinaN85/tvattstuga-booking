package se.martinanyberg.tvattstuga_booking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @NotBlank(message = "E-post får inte vara tom")
    @Pattern(
            regexp = "^[^@\\s]+@[^@\\s]+\\.(se|com|nu)$",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "E-post måste vara giltig och sluta på .se, .com eller .nu"
    )
    private String email;

    public User() {
    }

    public User(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
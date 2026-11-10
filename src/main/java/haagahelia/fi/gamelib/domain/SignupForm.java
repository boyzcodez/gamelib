package haagahelia.fi.gamelib.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

// Form-backing object for the register page. Not an entity:
// passwordCheck only exists to confirm the password and is never saved.
public class SignupForm {

    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be 3-30 characters")
    private String username = "";

    @NotEmpty(message = "Password is required")
    @Size(min = 5, max = 72, message = "Password must be 5-72 characters")
    private String password = "";

    @NotEmpty(message = "Please re-enter the password")
    private String passwordCheck = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordCheck() {
        return passwordCheck;
    }

    public void setPasswordCheck(String passwordCheck) {
        this.passwordCheck = passwordCheck;
    }
}

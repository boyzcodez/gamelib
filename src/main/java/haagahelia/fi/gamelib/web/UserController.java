package haagahelia.fi.gamelib.web;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import haagahelia.fi.gamelib.domain.AppUser;
import haagahelia.fi.gamelib.domain.AppUserRepo;
import haagahelia.fi.gamelib.domain.SignupForm;
import jakarta.validation.Valid;

@Controller
public class UserController {

    private final AppUserRepo userRepo;
    private final BCryptPasswordEncoder encoder;

    public UserController(AppUserRepo userRepo, BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    // Show the register form
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("signupform", new SignupForm());
        return "signup";
    }

    // Create the new user
    @PostMapping("/saveuser")
    public String saveUser(@Valid @ModelAttribute("signupform") SignupForm signupForm,
            BindingResult bindingResult) {

        String username = signupForm.getUsername().trim();

        // Passwords must match
        if (!signupForm.getPassword().equals(signupForm.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "err.passCheck", "Passwords do not match");
        }

        // Username must be free
        if (userRepo.findByUsername(username) != null) {
            bindingResult.rejectValue("username", "err.username", "Username already exists");
        }

        // Any errors -> back to the form
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        String hash = encoder.encode(signupForm.getPassword());
        userRepo.save(new AppUser(username, hash, "USER"));

        return "redirect:/login?registered";
    }
}

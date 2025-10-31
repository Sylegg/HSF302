package fa25.beitweb.controller;

import fa25.beitweb.entity.User;
import fa25.beitweb.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // -> templates/login.html
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        User user = userService.getUsersByEmailAndPassword(email, password);

        if (user != null) {
            session.setAttribute("user", user);
            if ("STAFF".equalsIgnoreCase(user.getRole())) {
                return "staff/dashboard";
            } else {
                return "member/dashboard";
            }
        } else {
            model.addAttribute("error", "Sai email hoặc mật khẩu!");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // templates/register.html
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String role,
                           Model model) {

        if (userService.exsitingEmail(email)) {
            model.addAttribute("error", "Email này đã được đăng ký!");
            return "register";
        }

        User user = User.builder()
                .fullName(fullName)
                .email(email)
                .password(password)
                .role(role.toUpperCase()) // STAFF hoặc MEMBER
                .build();

        userService.createUser(user);
        model.addAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "login";
    }
}


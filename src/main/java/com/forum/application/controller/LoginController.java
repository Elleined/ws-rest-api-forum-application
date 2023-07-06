package com.forum.application.controller;

import com.forum.application.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping
public class LoginController {
    private final UserService userService;

    @GetMapping
    public String gotoLogin(HttpSession session) {

        String email = (String) session.getAttribute("email");
        if (email != null) return "redirect:/forum";

        return "login";
    }

    @PostMapping
    public String login(@RequestParam String email,
                           HttpSession session,
                           Model model) {

        if (!userService.isEmailExists(email)) {
            model.addAttribute("emailNotExists", true);
            model.addAttribute("emailErrorMessage", "Email Not Exists");
            return "login";
        }

        session.setAttribute("email", email);
        log.debug("{} registered successfully", email);

        return "redirect:/forum";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        String email = (String) session.getAttribute("email");
        log.debug("{} logout Successfully", email);
        session.invalidate();

        return "redirect:/";
    }

    @ResponseBody
    @GetMapping("/dynamicPage")
    public ModelAndView dynamicView() {

        ModelAndView modelAndView = new ModelAndView("dynamic-page");
        List<String> strings = List.of("STRING1", "STRING2");
        modelAndView.addObject("strings", strings);
        modelAndView.addObject("myVariable", "Hello World!");

        return modelAndView;
    }
}

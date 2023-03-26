package com.nrifintech.client.controller;

import javax.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.nrifintech.model.ConfirmationToken;
import com.nrifintech.model.User;
import com.nrifintech.repository.ConfirmationTokenRepository;
import com.nrifintech.repository.UserRepository;
import com.nrifintech.service.EmailService;

@Controller
public class MyController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	ConfirmationTokenRepository confirmationTokenRepository;

	@Autowired
	EmailService emailService;

	@GetMapping("/home")
	public String home() {
		return "home";
	}

	@PostMapping("/do_register")
	public RedirectView registerUser(@Valid @ModelAttribute("user") User user, BindingResult res, Model m,
			RedirectAttributes ra) {
		try {

			if (res.hasErrors()) {
				System.out.println("Error");
				m.addAttribute("user", user);
				return new RedirectView("/home#signup");
			}
			if (userRepository.existsByEmail(user.getEmail())) {
				ra.addFlashAttribute("signup_msg", "User is already registered. Duplicate entry.");
			}
			user.setRole("ROLE_USER");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			User result = this.userRepository.save(user);
			System.out.println(result);
			ConfirmationToken confirmationToken = new ConfirmationToken(user);

			confirmationTokenRepository.save(confirmationToken);

			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(user.getEmail());
			mailMessage.setSubject("Complete Registration!");
			mailMessage.setText("To confirm your account, please click here : "
					+ "http://localhost:8080/confirm-account?token=" + confirmationToken.getConfirmationToken());
			emailService.sendEmail(mailMessage);

			System.out.println("Confirmation Token: " + confirmationToken.getConfirmationToken());

			m.addAttribute("user", new User());

		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("user", user);
			ra.addFlashAttribute("signup_msg", "User is already registered. Duplicate entry.");
			return new RedirectView("/home#signup");
		}
		ra.addFlashAttribute("signupSuccess_msg", "Successfully registered. Check your email for account activation link.");
		return new RedirectView("/home#signup");
	}

	@RequestMapping(value = "/confirm-account", method = { RequestMethod.GET, RequestMethod.POST })
	public RedirectView confirmUserAccount(@RequestParam("token") String confirmationToken, RedirectAttributes ra) {
		System.out.println("checking");
		ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

		if (token != null) {
			User user = userRepository.findByEmailIgnoreCase(token.getUserEntity().getEmail());
			user.setEnabled(true);
			userRepository.save(user);
			System.out.println("Email verified successfully!");
			ra.addFlashAttribute("verificationSuccess_msg", "Email verified successfully. You can login now");
		}
		else{
		    System.out.println("Error: Couldn't verify email");
		}
		return new RedirectView("/home#login");
	}

	@GetMapping("/login-error")
	public RedirectView login(RedirectAttributes ra) {
		ra.addFlashAttribute("login_msg", "Incorrect Username or password.");
		return new RedirectView("/home#login");
	}

	@GetMapping("/signupPage")
	public RedirectView signupPage() {
		return new RedirectView("/home#signup");
	}

	@GetMapping("/loginPage")
	public RedirectView loginPage() {
		return new RedirectView("/home#login");
	}
}

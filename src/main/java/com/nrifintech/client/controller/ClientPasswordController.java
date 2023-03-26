package com.nrifintech.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.nrifintech.service.UserService;

@Controller
public class ClientPasswordController {
	
	@Autowired
	private UserService userService;
	
	
	@GetMapping("/accountRecovery")
	public String accoutRecovery() {
		return "accountRecovery";
	}
	@PostMapping("/accountRecovery")
	public RedirectView accountRecovery(@RequestParam("searchText") String userName,RedirectAttributes redirAttrs){
		String response=userService.accountRecovery(userName).getBody();
		System.out.println(response);
		redirAttrs.addFlashAttribute("response", response);
		return new RedirectView("/accountRecovery");
	}
}

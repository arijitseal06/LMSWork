package com.nrifintech.config;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		CustomUserDetails customUserDetails=(CustomUserDetails)authentication.getPrincipal();
		String redirectUrl=request.getContextPath();
		if(customUserDetails.hasRole("ROLE_USER")){
			redirectUrl+="user/dashboard";
		}
		if(customUserDetails.hasRole("ROLE_ADMIN")){
			redirectUrl+="admin/dashboard";
		}
		response.sendRedirect(redirectUrl);
	}
}

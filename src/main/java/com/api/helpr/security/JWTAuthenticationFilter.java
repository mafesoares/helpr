package com.api.helpr.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.api.helpr.domain.dtos.CredenciaisDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	private AuthenticationManager authenticationManager;
	private JWTUtil jwtUtil;
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}


	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			CredenciaisDTO cred = new ObjectMapper().readValue(request.getInputStream(), CredenciaisDTO.class);
			UsernamePasswordAuthenticationToken authenticationTolken = new UsernamePasswordAuthenticationToken(
					cred.getEmail(), cred.getSenha(), new ArrayList<>());
			Authentication authentication = authenticationManager.authenticate(authenticationTolken);
			return authentication;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		String username = ((UserSS) authResult.getPrincipal()).getUsername();
		String token = jwtUtil.generetedTolken(username);
		response.setHeader("access-control-expose-headers", "Authorization");
		response.setHeader("Authorization", "Bearer " + token);
	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		response.setStatus(401);
		response.setContentType("aplication/json");
		response.getWriter().append(json());
	}

	private CharSequence json() {
		long date = new Date().getTime();
		return "{" 
				+ "\"timestamp\": " + date + ", " 
				+ "\"status\": 401, " 
				+ "\"error\": \"Não autorizado\", "
				+ "\"message\": \"Email ou senha inválidos\", " 
				+ "\"path\": \"/login\"}";
	}
	
}


//autentica se você pode entrar no domínio principal da aplicação














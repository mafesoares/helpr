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
	
	private AuthenticationManager authenticationManager;//principal interface pra autenticação. Se não for válido, vira nulo
	private JWTUtil jwtUtil;//se o usuário conseguir se autenticar, chamamos o generation token
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}
	
	//método que tenta autenticar o usuário
	//pega os valores passados na autenticação POST, converte em CredenciaisDTO, instancia um obj do tipo UsernamePassword e passa ele como parâmetro p método
	//authentication que contém o authenticationManager usando as classes UserDetails e UserSS
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			//request vem cheio de campo iguais ou diferentes das credenciais
			CredenciaisDTO creds = new ObjectMapper().readValue(request.getInputStream(), CredenciaisDTO.class);//lê os campos, mapeia os inputs, procura o que tem em comum com credenciaisdto. Quando encontra, dá um get (email/senha)
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(//response obj principal e cred criado a partir das credenciais
					creds.getEmail(),//importante p biulder no token
					creds.getSenha(),//importante p biulder no token
					new ArrayList<>());
			
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			return authentication;
			
		} catch (Exception e ) {
			throw new RuntimeException(e);//o java já tem um construtur com essa causa de erro
		}
	}
	
	//método de sucesso na autenticação
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		String username = ((UserSS) authResult.getPrincipal()).getUsername();//cria uma string e usa a userSS como base; tem o getPrincipal e seta o email
		String token = jwtUtil.generateToken(username);//classe do JWTUtil, passa o email, ele constrói o token
		response.setHeader("access-control-expose-headers", "Authorization");//seta o cabeçalho da resposta, coloca esse padrão access p saber o nível de autorização do token
		response.setHeader("Authorization", "Bearer"+ token);//seta o campo como autorization -- chamada no Postman; retorna o token p o usuário
	}
	
	//método de insucesso na autenticação; quando dá erro, ele recusa o json e cai aqui
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		response.setStatus(401);
		response.setContentType("aplication/json");//tipo do conteúdo
		response.getWriter().append(json());//body
	}
	private CharSequence json() {
		long date = new Date().getTime();
		return "{"
				+"\"timestamp\": "+date+","
				+"\"status\": 401, "
				+ "\"error\": \"Não autorizado\","
				+"\"message\": \"Email ou senha inválidos\","
				+"\"path\": \"/login\"}";
				
	}
	
}


//autentica se você pode entrar no domínio principal da aplicação














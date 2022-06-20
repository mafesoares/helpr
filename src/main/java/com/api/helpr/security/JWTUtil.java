package com.api.helpr.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component //p poder injetar posteriormente
public class JWTUtil {
	
	@Value("${jwt.expiration}")
	private Long expiration;
	
	@Value("${jwt.secret}")
	private String secret;
	
	//nesse momento, só autorizações = acesso básico
	public String generateToken(String email) {//método que vai gerar o token como string
		return Jwts.builder()//constrói a chave; definir valores:
				.setSubject(email)//add o email como parâmetro
				.setExpiration(new Date(System.currentTimeMillis() + expiration)) //soma do dia + x segundos(jwt.expiration), aí expira
				.signWith(SignatureAlgorithm.HS512, secret.getBytes())//converte o algoritmo a partir do 512, precisa do email e chave (embaralha tudo (email, chave, array))
				.compact();//compacta e converte numa chave
	}

	public boolean tokenValido(String token) {
		Claims claims = getClaims(token);//converte o token em uma articulação de json; validando todos os campos
		if(claims != null) {
			String username = claims.getSubject(); //coleta o email
			Date expirationDate = claims.getExpiration(); //valida o tempo; payload
			Date now = new Date(System.currentTimeMillis()); //hora atual
			
			if(username !=null && expirationDate != null && now.before(expirationDate)) {//email válido, não tá expirado, data não é menor que a atual -- valida tudo aqui
				return true;
			}	
		}
		return false;
	}

	private Claims getClaims(String token) {
		try {
			return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();//entregou a chave, abriu e viu que o token pertence a req
		} catch (Exception e) {
			return null;
		}
	}

	public String getUsername(String token) {
		Claims claims = getClaims(token);
		if(claims != null) {
			return claims.getSubject(); //email
		}
		return null;
	}
}
//cria a sessão
//opera o token = JWTUtil
















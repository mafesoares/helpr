package com.api.helpr.config;

import java.util.Arrays;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.api.helpr.security.JWTAuthenticationFilter;
import com.api.helpr.security.JWTUtil;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final String[] PUBLIC_MATCHERS = {"/h2-console/**"};//liberando o acesso ao h2

	@Autowired
	private Environment env;//interface que apresenta o ambiente onde estamos rodando
	
	@Autowired
	private JWTUtil jwtUtil;//parâmetro do http.addFilter
	
	@Autowired
	private UserDetailsService userDetailsService;//notação @Service
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {//qq endpoint que precisa de defesa
		
		if(Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();//se for perfil de teste, pode entrar, só registra numa sessão do enviroment
		}
		
		http.cors().and().csrf().disable(); //o usuário só loga quando a política de cors e security estevirem ok
		http.addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtUtil));//instalou o filtro criado (jwetUtil e o filtro (que recebe info que gera o token)
		http.authorizeRequests()//autorize todas as requisições desde que elas venham dos seguintes pontos:
		.antMatchers(PUBLIC_MATCHERS)//checa se ele faz parte do ponto comum (teste ou login)
		.permitAll()//permite
		.anyRequest().authenticated();//autorização do requisito@Autowired; p qq outra requisição tem que estar autenticado
			
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//constrói uma sessão (guard), assegura que a sessão de usuário não vai ser criada
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {//configure o detalhe, mas enconda o password
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}

	@Bean // uma exigência para ser executado na construção da aplicação
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();//aplica permissão de valores padrões
		configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));//ninguém pode fazer isso sem chave de seg
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();//toda vez que ele entra a url muda
		source.registerCorsConfiguration("/**", configuration);//registrando uma configuração de cors - todos os domínios
		return source;
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {//manda um dado
		return new BCryptPasswordEncoder(); //ele devolve em dado encriptado
	}
	
}
//todo endpoint passa no config

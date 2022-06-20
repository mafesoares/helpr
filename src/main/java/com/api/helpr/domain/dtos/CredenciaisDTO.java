package com.api.helpr.domain.dtos;

public class CredenciaisDTO {
	
	private String email;
	private String senha;
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
}

//não é trafegado na rede
//serve para conversão do usuário e senha que vem na requisição de login
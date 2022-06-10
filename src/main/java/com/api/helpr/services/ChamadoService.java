package com.api.helpr.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.helpr.domain.Chamado;
import com.api.helpr.repositories.ChamadoRepository;
import com.api.helpr.services.exceptions.ObjectNotFoundException;

@Service
public class ChamadoService {
	
	@Autowired
	private ChamadoRepository repository;

	public Chamado findById(Integer id) {
		Optional<Chamado> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Chamado não foi encontrado: " + id));
		
	}
	
	public List<Chamado> findAll(){
		return repository.findAll();
	}
}

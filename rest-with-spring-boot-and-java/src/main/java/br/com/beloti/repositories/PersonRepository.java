package br.com.beloti.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.beloti.model.Person;

@Repository // Anotação não mais necessária
public interface PersonRepository extends JpaRepository<Person, Long>{
    
}

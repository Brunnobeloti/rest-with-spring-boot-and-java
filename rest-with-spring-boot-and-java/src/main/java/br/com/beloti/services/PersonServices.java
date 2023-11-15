package br.com.beloti.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import br.com.beloti.controller.PersonController;
import br.com.beloti.data.vo.v1.PersonVO;
import br.com.beloti.data.vo.v2.PersonVOV2;
import br.com.beloti.exceptions.RequiredObjectIsNullException;
import br.com.beloti.exceptions.ResourceNotFoundException;
import br.com.beloti.mapper.DozerMapper;
import br.com.beloti.mapper.custom.PersonMapper;
import br.com.beloti.model.Person;
import br.com.beloti.repositories.PersonRepository;

@Service
public class PersonServices {
    
    private Logger logger = Logger.getLogger(PersonServices.class.getName());

    @Autowired
    PersonRepository repository;

    @Autowired
    PersonMapper mapper;

    public List<PersonVO> findAll() {
        
        logger.info("Finding all people");
        
        var persons = DozerMapper.parseListObject(repository.findAll(), PersonVO.class);
        persons.stream()
                .forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
        return persons;
    }



    public PersonVO findById(Long id) {

        logger.info("Finding one person");

        var entity = repository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        var vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public PersonVO create(PersonVO person) {
        
        if (person == null) throw new RequiredObjectIsNullException();
        logger.info("Creating one person!");

        var entity = DozerMapper.parseObject(person, Person.class);
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }
    // Reescrita do metodo para implementação da V2
    public PersonVOV2 createV2(PersonVOV2 person) {
        
        logger.info("Creating one person with V2!");

        var entity = mapper.convertVoToEntity(person);
        var vo = mapper.convertEntityToVo(repository.save(entity));
        return vo;
    }

    public PersonVO update(PersonVO person) {
        
        if (person == null) throw new RequiredObjectIsNullException();
        logger.info("Updating one person!");

        var entity = repository.findById(person.getKey())
                                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public void delete(Long id) {
        
        logger.info("Deleting one person!");

        var entity = repository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.delete(entity);
    }



}

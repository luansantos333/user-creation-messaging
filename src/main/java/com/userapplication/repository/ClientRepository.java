package com.userapplication.repository;

import com.userapplication.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {


    Optional<ClientEntity> findByClientEntityId(Long id);

    Optional<ClientEntity> findByClientId (String clientId);


}

package com.userapplication.repository;

import com.userapplication.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {


    Optional<ClientEntity> findById(Long id);

    @Query (value = "SELECT c.client_id FROM ClientEntity c WHERE c.client_id = ':client_id'")
    ClientEntity findByClientId(String client_id);


}

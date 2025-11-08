package com.userapplication.repository;

import com.userapplication.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    @Query
    Optional<ClientEntity> findById(@Param("id") Long id);

    @Query (value = "SELECT c FROM ClientEntity c WHERE c.client_id = :client_id")
    ClientEntity findByClientId(@Param("client_id") String client_id);


}

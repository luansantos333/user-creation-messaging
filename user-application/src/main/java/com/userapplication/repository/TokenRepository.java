package com.userapplication.repository;

import com.userapplication.entity.PassswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<PassswordResetTokenEntity, Long> {


    Optional<PassswordResetTokenEntity> findByToken(String token);

    void deleteByToken(String token);

}

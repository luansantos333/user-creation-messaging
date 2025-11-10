package com.userapplication.repository;

import com.userapplication.entity.PassswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<PassswordResetTokenEntity, Long> {



}

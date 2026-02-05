package com.aikyam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aikyam.model.MatrixMessage;

@Repository
public interface MatrixMessageRepository extends JpaRepository<MatrixMessage, Long> {

}

package com.mvanalytic.apirest_demo_springboot.repositories.business;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mvanalytic.apirest_demo_springboot.domain.business.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
  
}

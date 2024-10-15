package com.mvanalytic.apirest_demo_springboot.repositories.business;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mvanalytic.apirest_demo_springboot.domain.business.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Byte> {

}

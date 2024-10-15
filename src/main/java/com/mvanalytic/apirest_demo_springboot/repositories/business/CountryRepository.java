package com.mvanalytic.apirest_demo_springboot.repositories.business;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mvanalytic.apirest_demo_springboot.domain.business.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Byte>  {
  
}

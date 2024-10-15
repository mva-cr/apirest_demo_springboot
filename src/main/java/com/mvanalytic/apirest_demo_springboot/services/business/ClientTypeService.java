package com.mvanalytic.apirest_demo_springboot.services.business;

import com.mvanalytic.apirest_demo_springboot.repositories.business.ClientTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.mvanalytic.apirest_demo_springboot.domain.business.ClientType;
import com.mvanalytic.apirest_demo_springboot.utility.LoggerSingleton;
import org.apache.logging.log4j.Logger;

// Pendiente los DTO y los Try-catch
@Service
public class ClientTypeService {

   // Instancia singleton de logger
  private static final Logger logger = LoggerSingleton.getLogger(ClientTypeService.class);

  @Autowired
  private ClientTypeRepository clientTypeRepository;

  
  @Transactional
  public ClientType saveClientType(ClientType clientType){
    try {
      return clientTypeRepository.save(clientType);
    } catch (Exception e) {
      logger.error("Error al guardar el RefreshToken: {}", e.getMessage());
      throw new IllegalArgumentException("??, Error al guardar el ClientType");
    }
  }

  public List<ClientType> getAllClientType(){
    List<ClientType> cList = clientTypeRepository.findAll();
    return cList;
  }
}

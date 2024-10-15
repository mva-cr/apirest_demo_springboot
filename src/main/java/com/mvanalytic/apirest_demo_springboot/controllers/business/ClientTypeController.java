package com.mvanalytic.apirest_demo_springboot.controllers.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.mvanalytic.apirest_demo_springboot.domain.business.ClientType;
import com.mvanalytic.apirest_demo_springboot.services.business.ClientTypeService;

// Pendiente los DTO's
@RestController
@RequestMapping("/api/user-bussiness/client-type")
public class ClientTypeController {

  @Autowired
  private ClientTypeService cService;

  @GetMapping("/all")
  @PreAuthorize("hasAuthority('ROLE_USER')")
  public ResponseEntity<List<ClientType>> getAll() {
    List<ClientType> cList = cService.getAllClientType();
    return ResponseEntity.ok(cList);
  }

}

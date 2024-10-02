package com.mvanalytic.apirest_demo_springboot.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.mvanalytic.apirest_demo_springboot.services.user.UserSessionServiceImpl;

@RestController
@RequestMapping("/api/admin/user-sessions")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminSessionController {

  @Autowired
  private UserSessionServiceImpl userSessionServiceImpl;
  
}

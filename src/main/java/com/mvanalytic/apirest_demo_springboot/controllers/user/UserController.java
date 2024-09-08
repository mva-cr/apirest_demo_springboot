package com.mvanalytic.apirest_demo_springboot.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mvanalytic.apirest_demo_springboot.domain.user.User;
import com.mvanalytic.apirest_demo_springboot.dto.user.UserProfileDTO;
import com.mvanalytic.apirest_demo_springboot.mapper.user.UserMapper;
import com.mvanalytic.apirest_demo_springboot.services.user.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador para gestionar operaciones CRUD de usuarios.
 * Este controlador solo puede ser accedido por usuarios con el rol
 * "ROLE_ADMIN".
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping("/info/{nickname}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
  public ResponseEntity<UserProfileDTO> getUserInfo(@PathVariable String nickname) {
    User user = userService.getUserByNickName(nickname);
    UserProfileDTO userProfileDTO = new UserProfileDTO();
    userProfileDTO = UserMapper.convertUserToUserProfileDTO(user);
    return ResponseEntity.ok(userProfileDTO);
  }

}

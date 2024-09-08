package com.mvanalytic.apirest_demo_springboot.repositories.user;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mvanalytic.apirest_demo_springboot.domain.user.User;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Busca un usuario en la base de datos por su correo electrónico.
   *
   * @param email el correo electrónico del usuario que se desea encontrar.
   * @return un Optional que contiene el usuario si se encuentra, o vacío si no.
   */
  Optional<User> findByEmail(String email);

  /**
   * Busca un usuario en la base de datos por su nombre de usuario (nickname).
   *
   * @param nickname el nombre de usuario del usuario que se desea encontrar.
   * @return un Optional que contiene el usuario si se encuentra, o vacío si no.
   */
  Optional<User> findByNickname(String nickname);
  
}

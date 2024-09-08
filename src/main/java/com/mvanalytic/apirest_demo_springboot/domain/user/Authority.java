package com.mvanalytic.apirest_demo_springboot.domain.user;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/*
 * Representa una autoridad en el sistema
 */
@Entity
@Table(name = "authority", schema = "dbo")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Authority implements GrantedAuthority {
  private static final long serialVersionUID = 1L;

  /**
   * Nombre de la autoridad, que también es la clave primaria.
   */
  @Id
  @NotNull
  @Column(name = "name", length = 50, nullable = false)
  @Size(max = 50)
  private String name;

  // Constructor por defecto
  public Authority() {}

  // Constructor con parámetros
  public Authority(String name) {
      this.name = name;
  }

   // Implementación del método getAuthority() de GrantedAuthority
   @Override
   public String getAuthority() {
     return name;
   }
  // Getter y setter

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Authority other = (Authority) obj;
    return name != null ? name.equals(other.name) : other.name == null;
  }

  @Override
  public String toString() {
    return "Authority [name=" + name + "]";
  }

}

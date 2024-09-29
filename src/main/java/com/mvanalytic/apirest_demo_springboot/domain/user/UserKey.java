package com.mvanalytic.apirest_demo_springboot.domain.user;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/**
 * Entidad que representa la tabla 'user_key' en la base de datos.
 * 
 * Esta tabla se utiliza para almacenar claves relacionadas con los usuarios,
 * como claves de activación de cuenta o restablecimiento de contraseña. Cada
 * registro en esta tabla representa una clave única generada para un usuario
 * específico, con un propósito determinado (activación de cuenta o
 * restablecimiento de contraseña).
 */
@Entity
@Table(name = "user_key")
public class UserKey {

  /**
   * Identificador único de la clave (Primary Key).
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  /**
   * Relación de muchos a uno con la entidad User. Representa el usuario asociado
   * a esta clave. Esta columna es una clave foránea que referencia al id de la
   * tabla 'user_mva'.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_user", nullable = false)
  private User user;

  /**
   * Valor de la clave generado.
   * Puede ser utilizado para la activación de cuentas o el restablecimiento de
   * contraseñas. Tiene un tamaño máximo de 36 caracteres.
   */
  @Size(max = 36)
  @Column(name = "key_value", nullable = false, length = 36)
  private String keyValue;

  /**
   * Fecha y hora de creación de la clave.
   * Utiliza el tipo 'Instant' para manejar marcas de tiempo con precisión.
   */
  @Column(name = "created_at", columnDefinition = "DATETIME2", nullable = false)
  private Instant createdAt;

  /**
   * Propósito de la clave, que puede ser para 'ACCOUNT_ACTIVATION' o
   * 'PASSWORD_RESET'.
   * Indica el uso específico de la clave generada.
   */
  @Column(name = "key_purpose", nullable = false, length = 20)
  private String keyPurpose;

  // Constructores, Getters y Setters

  public UserKey() {
  }

  public UserKey(Long id, User user, String keyValue, Instant createdAt, String keyPurpose) {
    this.id = id;
    this.user = user;
    this.keyValue = keyValue;
    this.createdAt = createdAt;
    this.keyPurpose = keyPurpose;
  }

  /**
   * Obtiene el identificador único de la clave.
   * 
   * @return id de la clave.
   */
  public Long getId() {
    return id;
  }

  /**
   * Establece el identificador único de la clave.
   * 
   * @param id Identificador único de la clave.
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Obtiene el usuario asociado a esta clave.
   * 
   * @return Usuario al que pertenece esta clave.
   */
  public User getUser() {
    return user;
  }

  /**
   * Establece el usuario asociado a esta clave.
   * 
   * @param user Usuario al que pertenece esta clave.
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Obtiene el valor de la clave generada.
   * 
   * @return Valor de la clave.
   */
  public String getKeyValue() {
    return keyValue;
  }

  /**
   * Establece el valor de la clave generada.
   * 
   * @param keyValue Valor de la clave.
   */
  public void setKeyValue(String keyValue) {
    this.keyValue = keyValue;
  }

  /**
   * Obtiene la fecha y hora de creación de la clave.
   * 
   * @return Fecha y hora de creación de la clave.
   */
  public Instant getCreatedAt() {
    return createdAt;
  }

  /**
   * Establece la fecha y hora de creación de la clave.
   * 
   * @param createdAt Fecha y hora de creación de la clave.
   */
  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Obtiene el propósito de la clave.
   * 
   * @return Propósito de la clave ('ACCOUNT_ACTIVATION', 'PASSWORD_RESET', etc.).
   */
  public String getKeyPurpose() {
    return keyPurpose;
  }

  /**
   * Establece el propósito de la clave.
   * 
   * @param keyPurpose Propósito de la clave ('ACCOUNT_ACTIVATION',
   *                   'PASSWORD_RESET', etc.).
   */
  public void setKeyPurpose(String keyPurpose) {
    this.keyPurpose = keyPurpose;
  }

}

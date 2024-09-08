package com.mvanalytic.apirest_demo_springboot.domain.user;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*
 * Representa un usuario en el sistema
 */
@Entity
@Table(name = "user_mva", schema = "dbo")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User implements UserDetails {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(max = 50)
  @Column(name = "first_name", length = 50, nullable = false)
  private String firstName;

  @NotNull
  @Size(max = 50)
  @Column(name = "last_name", length = 50, nullable = false)
  private String lastName;

  @Size(max = 50)
  @Column(name = "second_last_name", length = 50, nullable = true)
  private String secondLastName;
  
  @NotNull
  @Email
  @Size(min = 5, max = 254)
  @Column(name = "email", length = 254, unique = true)
  private String email;

  /*
   * Caracteres permitidos:
   * Letras: A-Z, a-z
   * Dígitos: 0-9
   * Guion bajo: _
   * Punto: .
   * Arroba: @
   * Guion: -
   */
  @NotNull
  @Pattern(regexp = "^[_.@A-Za-z0-9-]*$")
  @Size(min = 1, max = 50)
  @Column(name = "nickname", length = 50, unique = true, nullable = false)
  private String nickname;

  @JsonProperty("password")
  @NotNull
  @Size(min = 60, max = 60)
  @Column(name = "password_hash", length = 60, nullable = false)
  private String password;

  /**
   * Define si el usuario está habilitado (true) o no (false)
   * Solo puede acceder si está habilitado
   */
  @NotNull
  @Column(name = "status", length = 1, nullable = false)
  private boolean status = true;

  @NotNull
  @Column(name = "activated", length = 1, nullable = false)
  private boolean activated = false;

  /**
   * Define la preferencia de idioma del usuario
   */
  @NotNull
  @Size(min = 2, max = 2)
  @Column(name = "language_key", length = 2, nullable = false)
  private String languageKey = "es";

  @Size(max = 36)
  @Column(name = "activation_key", length = 36)
  @JsonIgnore
  private String activationKey;

  @Size(max = 36)
  @Column(name = "reset_key", length = 36)
  @JsonIgnore
  private String resetKey;

  @Column(name = "reset_date", columnDefinition = "DATETIME2")
  private Instant resetDate = null;

  @JsonIgnore
  @ManyToMany(fetch = FetchType.EAGER) // Asegura que las autoridades se carguen con el usuario
  @JoinTable(name = "user_authority", schema = "dbo", joinColumns = {
      @JoinColumn(name = "user_id", referencedColumnName = "id")
  }, inverseJoinColumns = {
      @JoinColumn(name = "authority_name", referencedColumnName = "name")
  })
  @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  @BatchSize(size = 20)
  private Set<Authority> authorities = new HashSet<>();

  // Getters y setters

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getSecondLastName() {
    return secondLastName;
  }

  public void setSecondLastName(String secondLastName) {
    this.secondLastName = secondLastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public boolean isActivated() {
    return activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  public String getLanguageKey() {
    return languageKey;
  }

  public void setLanguageKey(String languageKey) {
    this.languageKey = languageKey;
  }

  public String getActivationKey() {
    return activationKey;
  }

  public void setActivationKey(String activationKey) {
    this.activationKey = activationKey;
  }

  public String getResetKey() {
    return resetKey;
  }

  public void setResetKey(String resetKey) {
    this.resetKey = resetKey;
  }

  public Instant getResetDate() {
    return resetDate;
  }

  public void setResetDate(Instant resetDate) {
    this.resetDate = resetDate;
  }

  public Set<Authority> getAuthoritySet() {
    return authorities;
  }

  public void setAuthorities(Set<Authority> authorities) {
    this.authorities = authorities;
  }

  // Implementación de métodos de UserDetails
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities.stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
        .collect(Collectors.toList());
  }

  @Override
  public String getUsername() {
    return this.nickname;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true; // Puede ajustar según su lógica
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.status;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return this.activated;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
    result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
    result = prime * result + ((secondLastName == null) ? 0 : secondLastName.hashCode());
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + (status ? 1231 : 1237);
    result = prime * result + (activated ? 1231 : 1237);
    result = prime * result + ((languageKey == null) ? 0 : languageKey.hashCode());
    result = prime * result + ((activationKey == null) ? 0 : activationKey.hashCode());
    result = prime * result + ((resetKey == null) ? 0 : resetKey.hashCode());
    result = prime * result + ((resetDate == null) ? 0 : resetDate.hashCode());
    result = prime * result + ((authorities == null) ? 0 : authorities.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    User other = (User) obj;
    return activated == other.activated &&
        (id != null ? id.equals(other.id) : other.id == null) &&
        (firstName != null ? firstName.equals(other.firstName) : other.firstName == null) &&
        (lastName != null ? lastName.equals(other.lastName) : other.lastName == null) &&
        (secondLastName != null ? secondLastName.equals(other.secondLastName) : other.secondLastName == null) &&
        (email != null ? email.equals(other.email) : other.email == null) &&
        (nickname != null ? nickname.equals(other.nickname) : other.nickname == null) &&
        (password != null ? password.equals(other.password) : other.password == null) &&
        (status == other.status) &&
        (languageKey != null ? languageKey.equals(other.languageKey) : other.languageKey == null) &&
        (activationKey != null ? activationKey.equals(other.activationKey) : other.activationKey == null) &&
        (resetKey != null ? resetKey.equals(other.resetKey) : other.resetKey == null) &&
        (resetDate != null ? resetDate.equals(other.resetDate) : other.resetDate == null) &&
        (authorities != null ? authorities.equals(other.authorities) : other.authorities == null);
  }

  @Override
  public String toString() {
    return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", secondLastName=" + secondLastName + ", email=" + email + ", nickname="
        + nickname + ", password=" + password + ", status=" + status + ", activated=" + activated + ", languageKey="
        + languageKey + ", activationKey=" + activationKey + ", resetKey=" + resetKey + ", resetDate=" + resetDate
        + ", authorities=" + authorities + "]";
  }

}

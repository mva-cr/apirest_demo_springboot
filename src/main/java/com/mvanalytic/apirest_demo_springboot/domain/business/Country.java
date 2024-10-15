package com.mvanalytic.apirest_demo_springboot.domain.business;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "country", schema = "dbo", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class Country {
  
  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Byte id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "phone_code", nullable = false)
    private Short phoneCode;

    public Country() {
    }

    public Byte getId() {
      return id;
    }

    public void setId(Byte id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Short getPhoneCode() {
      return phoneCode;
    }

    public void setPhoneCode(Short phoneCode) {
      this.phoneCode = phoneCode;
    }

    
}

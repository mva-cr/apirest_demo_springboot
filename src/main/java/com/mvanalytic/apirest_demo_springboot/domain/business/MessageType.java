package com.mvanalytic.apirest_demo_springboot.domain.business;


import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "message_type", schema = "dbo")
public class MessageType {
  
  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Byte id;

    @Column(name = "type", nullable = false, length = 12)
    private String type;

    public MessageType() {
    }

    public Byte getId() {
      return id;
    }

    public void setId(Byte id) {
      this.id = id;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
    
}

package com.mvanalytic.apirest_demo_springboot.dto.mail;

import java.util.List;
import java.util.Map;

/**
 * Clase DTO que representa un correo electrónico que se puede enviar a través de la
 * aplicación. Este objeto encapsula toda la información necesaria para
 * enviar un correo, incluyendo detalles como el remitente, destinatario, 
 * asunto, cuerpo del mensaje, y otros parámetros de configuración avanzada 
 * como adjuntos, copias, plantillas, etc.
 */
public class Mail {
  
  /**
     * Nombre completo del remitente del correo.
     */
    private String fullName;

    /**
     * Dirección de correo electrónico del remitente.
     */
    private String sender;

    /**
     * Dirección de correo electrónico del destinatario principal.
     */
    private String recipient;

    /**
     * Lista de direcciones de correo electrónico para enviar copia (CC).
     */
    private List<String> cc;

    /**
     * Lista de direcciones de correo electrónico para enviar copia oculta (BCC).
     */
    private List<String> bcc;

    /**
     * Asunto del correo electrónico.
     */
    private String subject;

    /**
     * Cuerpo del mensaje del correo electrónico.
     */
    private String message;

    /**
     * Lista de rutas o identificadores de archivos que se adjuntarán al correo.
     */
    private List<String> attachments;

    /**
     * Prioridad del correo electrónico (ejemplo: "High", "Normal", "Low").
     */
    private String priority;

    /**
     * Dirección de correo electrónico a la que se deben enviar las respuestas (Reply-To).
     */
    private String replyTo;

    /**
     * Nombre de la plantilla de correo que se utilizará para el contenido del mensaje.
     * Esto permite utilizar plantillas predefinidas para los correos electrónicos.
     */
    private String templateName;

    /**
     * Mapa de variables que serán utilizadas para personalizar el contenido de la plantilla del correo.
     * Las claves del mapa son los nombres de las variables en la plantilla y los valores son los datos 
     * que se insertarán en esas posiciones.
     */
    private Map<String, Object> variables;

    public Mail(String fullName, String sender, String recipient, List<String> cc, List<String> bcc, String subject,
        String message, List<String> attachments, String priority, String replyTo, String templateName,
        Map<String, Object> variables) {
      this.fullName = fullName;
      this.sender = sender;
      this.recipient = recipient;
      this.cc = cc;
      this.bcc = bcc;
      this.subject = subject;
      this.message = message;
      this.attachments = attachments;
      this.priority = priority;
      this.replyTo = replyTo;
      this.templateName = templateName;
      this.variables = variables;
    }

    public Mail() {
    }

    public String getFullName() {
      return fullName;
    }

    public void setFullName(String fullName) {
      this.fullName = fullName;
    }

    public String getSender() {
      return sender;
    }

    public void setSender(String sender) {
      this.sender = sender;
    }

    public String getRecipient() {
      return recipient;
    }

    public void setRecipient(String recipient) {
      this.recipient = recipient;
    }

    public List<String> getCc() {
      return cc;
    }

    public void setCc(List<String> cc) {
      this.cc = cc;
    }

    public List<String> getBcc() {
      return bcc;
    }

    public void setBcc(List<String> bcc) {
      this.bcc = bcc;
    }

    public String getSubject() {
      return subject;
    }

    public void setSubject(String subject) {
      this.subject = subject;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public List<String> getAttachments() {
      return attachments;
    }

    public void setAttachments(List<String> attachments) {
      this.attachments = attachments;
    }

    public String getPriority() {
      return priority;
    }

    public void setPriority(String priority) {
      this.priority = priority;
    }

    public String getReplyTo() {
      return replyTo;
    }

    public void setReplyTo(String replyTo) {
      this.replyTo = replyTo;
    }

    public String getTemplateName() {
      return templateName;
    }

    public void setTemplateName(String templateName) {
      this.templateName = templateName;
    }

    public Map<String, Object> getVariables() {
      return variables;
    }

    public void setVariables(Map<String, Object> variables) {
      this.variables = variables;
    }

    
}

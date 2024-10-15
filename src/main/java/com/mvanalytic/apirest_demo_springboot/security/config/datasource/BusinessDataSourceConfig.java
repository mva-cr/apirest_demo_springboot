
package com.mvanalytic.apirest_demo_springboot.security.config.datasource;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;

/**
 * Configuración del DataSource, EntityManagerFactory y TransactionManager para
 * la base de datos 'business'. Esta clase configura cómo Spring Boot se
 * conectará y manejará transacciones con la base de datos 'business', que está
 * separada de la base de datos primaria de la aplicación.
 *
 * La clase utiliza HikariCP como el pool de conexiones y define el
 * EntityManagerFactory y TransactionManager específicamente para la base de
 * datos 'business'.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.mvanalytic.apirest_demo_springboot.repositories.business", entityManagerFactoryRef = "businessEntityManagerFactory", transactionManagerRef = "businessTransactionManager")
public class BusinessDataSourceConfig {

  /**
   * Define el DataSource para la base de datos 'business' usando las propiedades
   * prefijadas con 'spring.datasource.business'. Se utiliza HikariCP como el pool
   * de conexiones.
   *
   * @return El DataSource configurado para la base de datos 'business'.
   */
  @Bean(name = "businessDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.business")
  public DataSource businessDataSource() {
    return DataSourceBuilder.create()
        .type(com.zaxxer.hikari.HikariDataSource.class)
        .build();
  }

  /**
   * Configura el EntityManagerFactory para la base de datos 'business',
   * vinculando el DataSource correspondiente y las entidades que están en el
   * paquete 'com.mvanalytic.apirest_demo_springboot.domain.business'.
   *
   * @param builder    El EntityManagerFactoryBuilder proporcionado por Spring.
   * @param dataSource El DataSource configurado para 'business'.
   * @return El EntityManagerFactory configurado para la base de datos 'business'.
   */
  @Bean(name = "businessEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean businessEntityManagerFactory(
      EntityManagerFactoryBuilder builder,
      @Qualifier("businessDataSource") DataSource dataSource) {
    return builder
        .dataSource(dataSource)
        .packages("com.mvanalytic.apirest_demo_springboot.domain.business")
        .persistenceUnit("business")
        .build(); // No es necesario agregar el dialecto aquí
  }

  /**
   * Define el TransactionManager para la base de datos 'business' utilizando el
   * EntityManagerFactory configurado. Se encarga de manejar las transacciones de
   * la base de datos 'business'.
   *
   * @param businessEntityManagerFactory El EntityManagerFactory vinculado a la
   *                                     base de datos 'business'.
   * @return El PlatformTransactionManager para gestionar las transacciones de
   *         'business'.
   */
  @Bean(name = "businessTransactionManager")
  public PlatformTransactionManager businessTransactionManager(
      @Qualifier("businessEntityManagerFactory") LocalContainerEntityManagerFactoryBean businessEntityManagerFactory) {

    EntityManagerFactory entityManagerFactory = businessEntityManagerFactory.getObject();
    if (entityManagerFactory == null) {
      throw new IllegalStateException("Business EntityManagerFactory is null");
    }

    return new JpaTransactionManager(entityManagerFactory);
  }
}


package com.mvanalytic.apirest_demo_springboot.security.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Configuración del DataSource, EntityManagerFactory y TransactionManager para
 * la base de datos 'customer'. Esta clase se encarga de configurar cómo Spring
 * Boot se conectará y manejará las transacciones con la base de datos
 * 'customer', que se utiliza para gestionar datos relacionados con los
 * usuarios, roles, etc.
 *
 * La clase utiliza HikariCP como pool de conexiones y define el
 * EntityManagerFactory y TransactionManager para la base de datos 'customer',
 * siendo esta la base de datos principal de la aplicación.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.mvanalytic.apirest_demo_springboot.repositories.user", entityManagerFactoryRef = "customerEntityManagerFactory", transactionManagerRef = "customerTransactionManager")
public class CustomerDataSourceConfig {

  /**
   * Define el DataSource principal para la base de datos 'customer' usando las
   * propiedades prefijadas con 'spring.datasource.customer'. Se utiliza HikariCP
   * como el pool de conexiones.
   *
   * @return El DataSource configurado para la base de datos 'customer'.
   */
  @Primary
  @Bean(name = "customerDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.customer")
  public DataSource customerDataSource() {
    return DataSourceBuilder.create()
        .type(com.zaxxer.hikari.HikariDataSource.class) // Asegura que HikariCP es el pool de conexiones
        .build();
  }

  /**
   * Configura el EntityManagerFactory para la base de datos 'customer',
   * vinculando el DataSource correspondiente y las entidades que están en el
   * paquete 'com.mvanalytic.apirest_demo_springboot.domain.user'. Este método se
   * asegura de que Spring JPA maneje correctamente las entidades relacionadas con
   * la base de datos 'customer'.
   *
   * @param builder    El EntityManagerFactoryBuilder proporcionado por Spring.
   * @param dataSource El DataSource configurado para 'customer'.
   * @return El EntityManagerFactory configurado para la base de datos 'customer'.
   */
  @Primary
  @Bean(name = "customerEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory(
      EntityManagerFactoryBuilder builder,
      @Qualifier("customerDataSource") DataSource dataSource) {
    return builder
        .dataSource(dataSource)
        .packages("com.mvanalytic.apirest_demo_springboot.domain.user") // Cambia a tu paquete correcto
        .persistenceUnit("customer")
        .build(); // No es necesario agregar el dialecto aquí
  }

  /**
   * Define el TransactionManager para la base de datos 'customer' utilizando el
   * EntityManagerFactory configurado. Este TransactionManager se encargará de
   * manejar las transacciones en la base de datos 'customer'.
   *
   * @param customerEntityManagerFactory El EntityManagerFactory vinculado a la
   *                                     base de datos 'customer'.
   * @return El PlatformTransactionManager para gestionar las transacciones de
   *         'customer'.
   */
  @Primary
  @Bean(name = "customerTransactionManager")
  public PlatformTransactionManager customerTransactionManager(
      @Qualifier("customerEntityManagerFactory") LocalContainerEntityManagerFactoryBean customerEntityManagerFactory) {
    EntityManagerFactory entityManagerFactory = customerEntityManagerFactory.getObject();
    if (entityManagerFactory == null) {
      throw new IllegalStateException("Customer EntityManagerFactory is null");
    }

    return new JpaTransactionManager(entityManagerFactory);
  }

}

package com.mvanalytic.apirest_demo_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// asegura que Spring escanee todos los componentes dentro del paquete
// com.mvanalytic.apirest_demo_springboot y sus subpaquetes, incluidos los
// repositorios.
@ComponentScan(basePackages = { "com.mvanalytic.apirest_demo_springboot" })
public class ApirestDemoSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApirestDemoSpringbootApplication.class, args);
	}

}

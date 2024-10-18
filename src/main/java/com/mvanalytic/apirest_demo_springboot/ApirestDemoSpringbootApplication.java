package com.mvanalytic.apirest_demo_springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import com.mvanalytic.apirest_demo_springboot.utility.AppUtility;

@SpringBootApplication
// asegura que Spring escanee todos los componentes dentro del paquete
// com.mvanalytic.apirest_demo_springboot y sus subpaquetes, incluidos los
// repositorios.
@ComponentScan(basePackages = { "com.mvanalytic.apirest_demo_springboot" })
public class ApirestDemoSpringbootApplication implements CommandLineRunner {

	@Autowired
	private AppUtility appUtility;

	public static void main(String[] args) {
		SpringApplication.run(ApirestDemoSpringbootApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/**
		 * Llama al método para crear el archivo de log si el perfil es `dev`. Esto
		 * asegura que el archivo de log y la carpeta correspondiente se creen durante
		 * la ejecución de la aplicación, únicamente en entornos de desarrollo. De esta
		 * manera, se mantiene una buena práctica al separar los entornos y evitar que
		 * en producción se generen logs no deseados.
		 */
		appUtility.createLogFile("sugef_test", "test_sugef.log");
	}

}

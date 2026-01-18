package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {

		// BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

        // String password = "myithri1@";
        // String hash = encoder.encode(password);

        // System.out.println("Password : " + password);
        // System.out.println("BCrypt   : " + hash);

		SpringApplication.run(BackendApplication.class, args);
	}

}

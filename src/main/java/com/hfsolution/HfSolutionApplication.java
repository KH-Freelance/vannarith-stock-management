package com.hfsolution;

import static com.hfsolution.feature.user.enums.Role.ADMIN;
import static com.hfsolution.feature.user.enums.Role.MANAGER;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import com.hfsolution.feature.auth.dto.RegisterRequest;
import com.hfsolution.feature.auth.services.AuthenticationService;


@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})

public class HfSolutionApplication {

	public static void main(String[] args) {
		SpringApplication.run(HfSolutionApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service
	) {
		return args -> {

			// var hak = RegisterRequest.builder()
			// 		.firstname("hak234567")
			// 		.lastname("zin234567")
			// 		.email("hakzin234567@mail.com")
			// 		.password("hakzin234567")
			// 		.role(ADMIN)
			// 		.build();
			// System.out.println("Hakzin token: " + service.register(hak).getAccessToken());


			// var hak2 = RegisterRequest.builder()
			// 		.firstname("hak")
			// 		.lastname("zin")
			// 		.email("hakzin@mail.com")
			// 		.password("hakzin")
			// 		.role(ADMIN)
			// 		.build();
			// System.out.println("Hakzin token: " + service.register(hak2).getAccessToken());

			// var admin = RegisterRequest.builder()
			// 		.firstname("Admin")
			// 		.lastname("Admin")
			// 		.email("admin@mail.com")
			// 		.password("password")
			// 		.role(ADMIN)
			// 		.build();
			// System.out.println("Admin token: " + service.register(admin).getAccessToken());

			// var manager = RegisterRequest.builder()
			// 		.firstname("Admin")
			// 		.lastname("Admin")
			// 		.email("manager@mail.com")
			// 		.password("password")

			// 		.role(MANAGER)
			// 		.build();
			// System.out.println("Manager token: " + service.register(manager).getAccessToken());


			// var sour = RegisterRequest.builder()
			// 		.firstname("tann")
			// 		.lastname("kimsour")
			// 		.email("sour@mail.com")
			// 		.password("password")

			// 		.role(MANAGER)
			// 		.build();
			// System.out.println("Manager token: " + service.register(sour).getAccessToken());

		};
	}
}

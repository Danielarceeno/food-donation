package com.example.donation;

import com.example.donation.entity.User;
import com.example.donation.entity.UserType;
import com.example.donation.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class DoacaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoacaoApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserRepository userRepository) {
		return args -> {
			User user = User.builder()
					.nomeCompleto("Daniel Arceno")
					.email("daniel7ar@gmail.com")
					.senha(new BCryptPasswordEncoder().encode("123"))
					.tipo(UserType.DOADOR)
					.cidade("São Paulo")
					.build();

			userRepository.save(user);

			System.out.println("Usuário salvo com sucesso!");
		};
	}
}
package com.example.movies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import com.example.movies.domain.*;

import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MoviesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoviesApplication.class, args);
	}

	@Bean
	public CommandLineRunner bookDemo(
			MovieRepository mRepository,
			WatchStatusRepository sRepository,
			AppUserRepository userRepository,
			GenreRepository gRepository,

			PasswordEncoder passwordEncoder) {
		return (args) -> {
			sRepository.save(new WatchStatus("Unwatched"));
			sRepository.save(new WatchStatus("Watched"));
			sRepository.save(new WatchStatus("Watching"));
			sRepository.save(new WatchStatus("Want to Watch"));
			sRepository.save(new WatchStatus("Dropped"));

			gRepository.save(new Genre("Horror"));
			gRepository.save(new Genre("Drama"));
			gRepository.save(new Genre("Comedy"));
			gRepository.save(new Genre("Science Fiction"));
			gRepository.save(new Genre("Thriller"));
			gRepository.save(new Genre("Kids"));
			gRepository.save(new Genre("Action"));
			gRepository.save(new Genre("Adventure"));
			gRepository.save(new Genre("Other"));
			
			/* 
			if (userRepository.findByUsername("user") == null) {
				userRepository.save(
						new AppUser("user", passwordEncoder.encode("user"), "USER", "user@example.com"));
			}

			if (userRepository.findByUsername("admin") == null) {
				userRepository.save(
						new AppUser("admin", passwordEncoder.encode("admin"), "ADMIN", "admin@example.com"));
			}
			*/
		};

	}

}


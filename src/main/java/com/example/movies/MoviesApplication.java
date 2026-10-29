package com.example.movies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import com.example.movies.domain.MovieRepository;
import com.example.movies.domain.Movie;
import com.example.movies.domain.WatchStatusRepository;
import com.example.movies.domain.WatchStatus;
import com.example.movies.domain.AppUser;
import com.example.movies.domain.AppUserRepository;

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
			PasswordEncoder passwordEncoder) {
		return (args) -> {
			sRepository.save(new WatchStatus("Unwatched"));
			sRepository.save(new WatchStatus("Watched"));
			sRepository.save(new WatchStatus("Watching"));
			sRepository.save(new WatchStatus("Want to Watch"));
			sRepository.save(new WatchStatus("Dropped"));

			if (userRepository.findByUsername("user") == null) {
				userRepository.save(
						new AppUser("user", passwordEncoder.encode("user"), "USER", "user@example.com"));
			}

			if (userRepository.findByUsername("admin") == null) {
				userRepository.save(
						new AppUser("admin", passwordEncoder.encode("admin"), "ADMIN", "admin@example.com"));
			}
		};
	}

}

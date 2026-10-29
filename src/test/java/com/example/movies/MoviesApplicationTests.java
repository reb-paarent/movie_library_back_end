package com.example.movies;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.movies.domain.AppUser;
import com.example.movies.domain.AppUserRepository;
import com.example.movies.domain.Movie;
import com.example.movies.domain.MovieRepository;
import com.example.movies.domain.WatchStatus;
import com.example.movies.domain.WatchStatusRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MoviesApplicationTests {

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private AppUserRepository userRepository;

	@Autowired
	private WatchStatusRepository statusRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void movieListCanBeFilteredByOwner() {
		AppUser alice = userRepository.save(new AppUser("alice", "password", "USER", "alice@example.com"));
		AppUser bob = userRepository.save(new AppUser("bob", "password", "USER", "bob@example.com"));
		WatchStatus status = statusRepository.save(new WatchStatus("Test status"));

		Movie alicesMovie = new Movie("Alice's movie", 2024, "Alice", status);
		alicesMovie.setOwner(alice);
		movieRepository.save(alicesMovie);

		Movie bobsMovie = new Movie("Bob's movie", 2024, "Bob", status);
		bobsMovie.setOwner(bob);
		movieRepository.save(bobsMovie);

		assertEquals(1, movieRepository.findByOwner_Username("alice").size());
		assertEquals("Alice's movie", movieRepository.findByOwner_Username("alice").get(0).getTitle());
	}
}

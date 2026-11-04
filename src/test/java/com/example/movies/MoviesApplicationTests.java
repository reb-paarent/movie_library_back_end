package com.example.movies;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.movies.domain.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MoviesApplicationTests {

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private AppUserRepository userRepository;

	@Autowired
	private WatchStatusRepository statusRepository;

	@Autowired
	private GenreRepository genreRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void movieListCanBeFilteredByOwner() {
		AppUser alice = userRepository.save(new AppUser("alice", "password", "USER", "alice@example.com"));
		AppUser bob = userRepository.save(new AppUser("bob", "password", "USER", "bob@example.com"));
		WatchStatus status = statusRepository.save(new WatchStatus("Test status"));
		Genre genre = genreRepository.save(new Genre("Test genre"));

		Movie alicesMovie = new Movie("Alice's movie", 2024, "Alice", status, genre);
		alicesMovie.setOwner(alice);
		movieRepository.save(alicesMovie);

		Movie bobsMovie = new Movie("Bob's movie", 2024, "Bob", status, genre);
		bobsMovie.setOwner(bob);
		movieRepository.save(bobsMovie);

		assertEquals(1, movieRepository.findByOwner_Username("alice").size());
		assertEquals("Alice's movie", movieRepository.findByOwner_Username("alice").get(0).getTitle());
	}
}

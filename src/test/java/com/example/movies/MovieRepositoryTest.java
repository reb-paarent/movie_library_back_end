package com.example.movies;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.movies.domain.*;

@DataJpaTest
@Import(MovieRepositoryTest.TestConfig.class)

public class MovieRepositoryTest {
    @Autowired
    private MovieRepository repository;
    @Autowired
    private GenreRepository gRepository;
    @Autowired
    private WatchStatusRepository wRepository;
    @Autowired
    private AppUserRepository userRepository;

    @TestConfiguration(proxyBeanMethods = false)
    static class TestConfig {
        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Test
    public void createNewMovie() {
        AppUser owner = userRepository.save(new AppUser("movie-owner", "password", "USER", "owner@example.com"));
        Genre genre = new Genre("Fantasy");
        gRepository.save(genre);
        WatchStatus status = new WatchStatus("Watch");
        wRepository.save(status);
        Movie movie = new Movie("Movie 6", 2009, "Person", status, genre);
        movie.setOwner(owner);
        repository.save(movie);
        assertThat(movie.getId()).isNotNull();
    }

    @Test
    public void editNewMovie() {
        AppUser owner = userRepository.save(new AppUser("movie-editor", "password", "USER", "editor@example.com"));
        Genre originalGenre = gRepository.save(new Genre("Fantasy"));
        WatchStatus originalStatus = wRepository.save(new WatchStatus("Watch"));
        Movie movie = new Movie("Original title", 2009, "Original director", originalStatus, originalGenre);
        movie.setOwner(owner);
        movie = repository.save(movie);

        Genre updatedGenre = gRepository.save(new Genre("Drama"));
        WatchStatus updatedStatus = wRepository.save(new WatchStatus("Watched"));
        movie.setTitle("Updated title");
        movie.setReleaseYear(2024);
        movie.setDirector("Updated director");
        movie.setGenre(updatedGenre);
        movie.setWatchStatus(updatedStatus);
        repository.save(movie);

        Movie editedMovie = repository.findById(movie.getId()).orElseThrow();
        assertThat(editedMovie.getTitle()).isEqualTo("Updated title");
        assertThat(editedMovie.getReleaseYear()).isEqualTo(2024);
        assertThat(editedMovie.getDirector()).isEqualTo("Updated director");
        assertThat(editedMovie.getGenre().getName()).isEqualTo("Drama");
        assertThat(editedMovie.getWatchStatus().getName()).isEqualTo("Watched");
    }

    @Test
    public void deleteNewMovie() {
        AppUser alice = userRepository.save(new AppUser("alice", "password", "USER", "alice@example.com"));
        Genre genre = gRepository.save(new Genre("Fantasy"));
        WatchStatus status = wRepository.save(new WatchStatus("Watch"));
        Movie movie = new Movie("Movie to delete", 2009, "Person", status, genre);
        movie.setOwner(alice);
        movie = repository.save(movie);

        Movie savedMovie = repository.findByIdAndOwner_Username(movie.getId(), alice.getUsername()).orElseThrow();
        repository.delete(savedMovie);

        assertThat(repository.findById(movie.getId())).isEmpty();
    }
    
}

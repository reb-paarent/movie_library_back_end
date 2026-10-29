package com.example.movies.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface MovieRepository extends CrudRepository<Movie, Long> {
    List<Movie> findByOwner_Username(String username);

    List<Movie> findByWatchStatus_NameAndOwner_Username(String name, String username);

    Optional<Movie> findByIdAndOwner_Username(Long id, String username);
}

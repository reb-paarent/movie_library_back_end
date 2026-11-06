package com.example.movies.domain;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.query.Param;

@RepositoryRestResource
public interface GenreRepository extends CrudRepository<Genre, Long> {
    List<Genre> findByName(@Param("name") String name);
}

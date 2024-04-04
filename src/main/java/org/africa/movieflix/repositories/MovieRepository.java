package org.africa.movieflix.repositories;

import org.africa.movieflix.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
}

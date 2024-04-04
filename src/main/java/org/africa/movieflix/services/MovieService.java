package org.africa.movieflix.services;

import org.africa.movieflix.dto.MovieDTO;
import org.africa.movieflix.dto.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {
    MovieDTO addMovie(MovieDTO movieDTO, MultipartFile file) throws IOException;
    MovieDTO getMovie(Integer movieId);
    List<MovieDTO> getAllMovies();
    MovieDTO updateMovies(Integer moviesId, MovieDTO movieDTO, MultipartFile file) throws IOException;
    String deleteMovie(Integer movieId) throws IOException;
    MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);
    MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber,
                                                           Integer pageSize, String sortBy, String direction);
}

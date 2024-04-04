package org.africa.movieflix.services;

import jakarta.validation.Valid;
import org.africa.movieflix.dto.MovieDTO;
import org.africa.movieflix.dto.MoviePageResponse;
import org.africa.movieflix.exceptions.MovieNotFoundException;
import org.africa.movieflix.models.Movie;
import org.africa.movieflix.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImplement implements MovieService{

    private final MovieRepository movieRepository;
    private final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImplement(MovieRepository movieRepository,
                                 FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDTO addMovie(@Valid MovieDTO movieDTO,
                             MultipartFile file) throws IOException {
        if (Files.exists(Paths.get(path + File.separator +
                file.getOriginalFilename()))){
            throw new FileAlreadyExistsException("A file with the same name already exist, " +
                    "please enter another file name");
        }
        String uploadedFileName = fileService.uploadFile(path, file);
        movieDTO.setPoster(uploadedFileName);

        Movie movie = new Movie(
                null,
                movieDTO.getTitle(),
                movieDTO.getDirector(),
                movieDTO.getStudio(),
                movieDTO.getMovieCast(),
                movieDTO.getReleaseYear(),
                movieDTO.getPoster()
        );

        Movie savedMovie = movieRepository.save(movie);

        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        return new MovieDTO(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );
    }

    @Override
    public MovieDTO getMovie(Integer movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(
                ()-> new MovieNotFoundException("Movie with id: " + movieId + " not found !"));

        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        return new MovieDTO(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();

        return movieDTOs(movies);
    }

    @Override
    public MovieDTO updateMovies(Integer movieId, MovieDTO movieDTO,
                                 MultipartFile file) throws IOException {
        Movie movie = movieRepository.findById(movieId).orElseThrow(
                ()-> new MovieNotFoundException("Movie with id: " + movieId + " not found !"));

        String fileName = movie.getPoster();
        if (file != null){
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        movieDTO.setPoster(fileName);

        Movie movieToSave = new Movie(
                movie.getMovieId(),
                movieDTO.getTitle(),
                movieDTO.getDirector(),
                movieDTO.getStudio(),
                movieDTO.getMovieCast(),
                movieDTO.getReleaseYear(),
                movieDTO.getPoster()
        );

        Movie updatedMovie = movieRepository.save(movieToSave);

        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        return new MovieDTO(
                updatedMovie.getMovieId(),
                updatedMovie.getTitle(),
                updatedMovie.getDirector(),
                updatedMovie.getStudio(),
                updatedMovie.getMovieCast(),
                updatedMovie.getReleaseYear(),
                updatedMovie.getPoster(),
                posterUrl
        );
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        Movie movie = movieRepository.findById(movieId).orElseThrow(
                ()-> new MovieNotFoundException("Movie with id: " + movieId + " not found !"));
        Integer id = movie.getMovieId();

        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));

        movieRepository.delete(movie);

        return "Movie deleted with id: " + id;
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Movie> moviePages = movieRepository.findAll(pageable);

        List<Movie> movies = moviePages.getContent();
        List<MovieDTO> movieDTOs = movieDTOs(movies);;

        return new MoviePageResponse(movieDTOs, pageNumber, pageSize,
                                        moviePages.getTotalElements(),
                                        moviePages.getTotalPages(),
                                        moviePages.isLast());
    }

    private List<MovieDTO> movieDTOs(List<Movie> movies) {
        List<MovieDTO> movieDTOs = new ArrayList<>();

        for(Movie movie : movies){
            String posterUrl = baseUrl + "/file/" + movie.getPoster();

            MovieDTO movieDTO = new MovieDTO(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDTOs.add(movieDTO);
        }
        return movieDTOs;
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                                  String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                                                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Movie> moviePages = movieRepository.findAll(pageable);

        List<Movie> movies = moviePages.getContent();
        List<MovieDTO> movieDTOs = movieDTOs(movies);;

        return new MoviePageResponse(movieDTOs, pageNumber, pageSize,
                moviePages.getTotalElements(),
                moviePages.getTotalPages(),
                moviePages.isLast());
    }
}

package org.africa.movieflix.contollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.africa.movieflix.dto.MovieDTO;
import org.africa.movieflix.dto.MoviePageResponse;
import org.africa.movieflix.exceptions.EmptyFileException;
import org.africa.movieflix.services.MovieService;
import org.africa.movieflix.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDTO> addMovieHandler(@RequestPart MultipartFile file,
                                                    @RequestPart String movieDTO) throws IOException {
        if (file.isEmpty()) {
            throw  new EmptyFileException("File is empty, please attach another file");
        }
        MovieDTO dto = convertToMovieDTO(movieDTO);
        return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);

    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDTO> getMovieHandler(@PathVariable Integer movieId){
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDTO>> getALlMovieHandler(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDTO> updateMovieHandler(@PathVariable Integer movieId,
                                                       @RequestPart MultipartFile file,
                                                       @RequestPart String movieDtoObj) throws IOException {
        if (file.isEmpty()) file = null;
        MovieDTO movieDTO = convertToMovieDTO(movieDtoObj);
        return ResponseEntity.ok(movieService.updateMovies(movieId, movieDTO, file));
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(
            @PathVariable Integer movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }

    @GetMapping("/allMoviePage")
    public ResponseEntity<MoviePageResponse> getMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize
    ) {
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(
                pageNumber, pageSize));
    }


    @GetMapping("/allMoviePageSort")
    public ResponseEntity<MoviePageResponse> getMoviesWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_by, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String direction
    ) {
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(
                pageNumber, pageSize, sortBy, direction));
    }


    private MovieDTO convertToMovieDTO(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj, MovieDTO.class);
    }
}

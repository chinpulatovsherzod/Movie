package com.example.movieapi.service;

import com.example.movieapi.dto.MovieDto;
import com.example.movieapi.dto.MoviePageResponse;
import com.example.movieapi.entities.Movie;
import com.example.movieapi.exceptions.FileExistsExceptions;
import com.example.movieapi.exceptions.MovieNotFoundExceptions;
import com.example.movieapi.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;

    private final FileService fileService;
    private final HandlerMapping resourceHandlerMapping;
    private String path;
    private String baseUrl;


    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        if (Files.exists(Paths.get(path + file.getOriginalFilename()))){
            throw new FileExistsExceptions("File already exists! Please enter another filename");
        }
        String uploadedFileName = fileService.uploadFile(path, file);
        movieDto.setPoster(uploadedFileName);

        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );


        Movie savedMovie = movieRepository.save(movie);

        String posterUrl = baseUrl + "/file/" + savedMovie.getPoster();

         MovieDto response = new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );
        return response;
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundExceptions("Movie not found with id =" + movieId));
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
        return response;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> movieDtos = new ArrayList<>();

        for (Movie movie: movies){
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
            movieDtos.add(movieDto);
        }
        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        Movie mv = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundExceptions("Movie not found with id =" + movieId));

        String fileName = mv.getPoster();
        if (file != null){
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        movieDto.setPoster(fileName);

        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        Movie updateMovie = movieRepository.save(movie);


        String posterUrl = baseUrl + "/file/" + fileName;

        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        Movie mv = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundExceptions("Movie not found with id = " + movieId));
        Integer id = mv.getMovieId();

        Files.deleteIfExists(Paths.get(path + File.separator + mv.getPoster()));

        movieRepository.delete(mv);
        return "Movie delete with id:" + id;
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Movie> page = movieRepository.findAll(pageable);
        List<Movie> movies = page.getContent();

        List<MovieDto> movieDtoList = new ArrayList<>();

        for (Movie movie: movies){
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
                MovieDto movieDto = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
            );
                movieDtoList.add(movieDto);

        }
        return new MoviePageResponse(movieDtoList, pageNumber, pageSize,
                page.getTotalPages(),
                page.getTotalElements(), page.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Movie> page = movieRepository.findAll(pageable);
        List<Movie> movies = page.getContent();

        List<MovieDto> movieDtoList = new ArrayList<>();
        for (Movie movie: movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtoList.add(movieDto);
        }
        return new MoviePageResponse(movieDtoList, pageNumber, pageSize,
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}

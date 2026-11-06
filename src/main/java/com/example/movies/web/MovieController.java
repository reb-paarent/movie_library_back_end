package com.example.movies.web;

import com.example.movies.domain.GenreRepository;
import java.util.List;
import java.util.Optional;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.movies.domain.MovieRepository;
import com.example.movies.domain.Movie;
import com.example.movies.domain.WatchStatusRepository;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.movies.domain.AppUser;
import com.example.movies.domain.AppUserRepository;

@Controller
public class MovieController {
    private MovieRepository repository;
    private WatchStatusRepository sRepository;
    private AppUserRepository userRepository;
    private GenreRepository gRepository;

    public MovieController(MovieRepository repository, WatchStatusRepository sRepository,
            AppUserRepository userRepository, GenreRepository gRepository) {
        this.repository = repository;
        this.sRepository = sRepository;
        this.userRepository = userRepository;
        this.gRepository = gRepository;
    }

    @GetMapping({ "/", "/index" })
    public String index() {
        return "index";
    }

    @GetMapping("/userhomepage")
    public String userHomepage() {
        return "userhomepage";
    }

    // Show all movies
    @RequestMapping(value = "/movielist")
    public String movieList(Model model, Principal principal) {
        model.addAttribute("movies", repository.findByOwner_Username(principal.getName()));
        return "movielist";
    }

    // Show all movies sorted by release year
    @RequestMapping(value = "/sortyear")
    public String movieListByYear(Model model, Principal principal) {
        List<Movie> movies = repository.findByOwner_UsernameOrderByReleaseYearAsc(principal.getName());
        model.addAttribute("movies", movies);
        return "movielist";
    }

    // Show all movies sorted by title
    @RequestMapping(value = "/sorttitle")
    public String movieListByTitle(Model model, Principal principal) {
        List<Movie> movies = repository.findByOwner_UsernameOrderByTitleAsc(principal.getName());
        model.addAttribute("movies", movies);
        return "movielist";
    }

    // Add new movie
    @RequestMapping(value = "/add")
    public String addMovie(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("watchStatuses", sRepository.findAll());
        model.addAttribute("genres", gRepository.findAll());
        return "addmovie";
    }

    // Save movie
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveMovie(Movie movie, Principal principal) {
        if (movie.getId() == null) {
            movie.setOwner(findUser(principal.getName()));
            repository.save(movie);
        } else {
            Movie existingMovie = findOwnedMovie(movie.getId(), principal.getName());
            existingMovie.setTitle(movie.getTitle());
            existingMovie.setReleaseYear(movie.getReleaseYear());
            existingMovie.setDirector(movie.getDirector());
            existingMovie.setWatchStatus(movie.getWatchStatus());
            existingMovie.setGenre(movie.getGenre());
            repository.save(existingMovie);
        }
        return "redirect:/movielist";
    }

    // Delete movie
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deleteMovie(@PathVariable("id") Long id, Principal principal) {
        repository.delete(findOwnedMovie(id, principal.getName()));
        return "redirect:/movielist";
    }

    // Edit movie
    @RequestMapping(value = "/edit/{id}")
    public String editMovie(@PathVariable("id") Long id, Model model, Principal principal) {
        Movie movie = findOwnedMovie(id, principal.getName());
        model.addAttribute("movie", movie);
        model.addAttribute("watchStatuses", sRepository.findAll());
        model.addAttribute("genres", gRepository.findAll());
        return "editmovie";
    }

    // Return all movies REST
    @RequestMapping(value = "/movies", method = RequestMethod.GET)
    public @ResponseBody List<Movie> movieListRest(Principal principal) {
        return repository.findByOwner_Username(principal.getName());
    }

    // Return movie by id REST
    @RequestMapping(value = "/movie/{id}", method = RequestMethod.GET)
    public @ResponseBody Optional<Movie> findMovieRest(@PathVariable("id") Long movieId, Principal principal) {
        return repository.findByIdAndOwner_Username(movieId, principal.getName());
    }

    // Filter movie list to only show movies with the "Want to Watch" status
    @RequestMapping(value = "/wanttowatch", method = RequestMethod.GET)
    public String wantToWatch(Model model, Principal principal) {
        model.addAttribute("movies",
                repository.findByWatchStatus_NameAndOwner_Username("Want to Watch", principal.getName()));
        return "wanttowatch";
    }

    /*
     * Filter movie list to show movies of specific genres.
     * Genre pages are found on the homepage.
     */
    private String genreMovies(String genre, String view, Model model, Principal principal) {
        model.addAttribute("movies", repository.findByGenre_NameAndOwner_Username(genre, principal.getName()));
        return view;
    }

    @GetMapping("/action")
    public String actionMovies(Model model, Principal principal) {
        return genreMovies("Action", "action", model, principal);
    }

    @GetMapping("/adventure")
    public String adventureMovies(Model model, Principal principal) {
        return genreMovies("Adventure", "adventure", model, principal);
    }

    @GetMapping("/comedy")
    public String comedyMovies(Model model, Principal principal) {
        return genreMovies("Comedy", "comedy", model, principal);
    }

    @GetMapping("/drama")
    public String dramaMovies(Model model, Principal principal) {
        return genreMovies("Drama", "drama", model, principal);
    }

    @GetMapping("/horror")
    public String horrorMovies(Model model, Principal principal) {
        return genreMovies("Horror", "horror", model, principal);
    }

    @GetMapping("/kids")
    public String kidsMovies(Model model, Principal principal) {
        return genreMovies("Kids", "kids", model, principal);
    }

    @GetMapping("/scifi")
    public String scienceFictionMovies(Model model, Principal principal) {
        return genreMovies("Science Fiction", "scifi", model, principal);
    }

    @GetMapping("/thriller")
    public String thrillerMovies(Model model, Principal principal) {
        return genreMovies("Thriller", "thriller", model, principal);
    }

    @GetMapping("/other")
    public String otherMovies(Model model, Principal principal) {
        return genreMovies("Other", "other", model, principal);
    }

    // Find owners or throw exceptions for missing owners when saving movies
    private Movie findOwnedMovie(Long id, String username) {
        return repository.findByIdAndOwner_Username(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private AppUser findUser(String username) {
        AppUser user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalStateException("No application user exists for authenticated username " + username);
        }
        return user;
    }

}

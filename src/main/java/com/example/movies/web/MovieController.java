package com.example.movies.web;

import java.util.List;
import java.util.Optional;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.movies.domain.MovieRepository;
import com.example.movies.domain.Movie;
import com.example.movies.domain.WatchStatusRepository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.movies.domain.AppUser;
import com.example.movies.domain.AppUserRepository;

@Controller
public class MovieController {
    @Autowired
    private MovieRepository repository;

    @Autowired
    private WatchStatusRepository sRepository;

    @Autowired
    private AppUserRepository userRepository;

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/userhomepage")
    public String userHomepage() {
        return "userhomepage";
    }

    @GetMapping("/usersettings")
    public String userSettings() {
        return "usersettings";
    }

    // Show all movies
    @RequestMapping(value = "/movielist")
    public String movieList(Model model, Principal principal) {
        model.addAttribute("movies", repository.findByOwner_Username(principal.getName()));
        return "movielist";
    }

    // Add new movie
    @RequestMapping(value = "/add")
    public String addMovie(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("watchStatuses", sRepository.findAll());
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
            repository.save(existingMovie);
        }
        return "redirect:/movielist";
    }

    // Delete movie
    @PreAuthorize("hasAuthority('ADMIN')")
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

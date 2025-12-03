package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) String idParam,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Ahoy! Search request received - name: '{}', id: '{}', genre: '{}'", name, idParam, genre);
        
        // Validate search parameters with pirate flair
        String validationError = movieService.validateSearchParameters(name, idParam, genre);
        if (validationError != null) {
            logger.warn("Search validation failed: {}", validationError);
            model.addAttribute("errorMessage", validationError);
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", idParam);
            model.addAttribute("searchGenre", genre);
            return "movies";
        }

        // Parse ID parameter if provided
        Long id = null;
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                id = Long.parseLong(idParam.trim());
            } catch (NumberFormatException e) {
                // This should not happen due to validation, but handle gracefully
                logger.error("Unexpected ID parsing error: {}", e.getMessage());
                model.addAttribute("errorMessage", "Arrr! That be not a valid movie ID, ye landlubber!");
                model.addAttribute("movies", movieService.getAllMovies());
                return "movies";
            }
        }

        // Perform the search
        List<Movie> searchResults = movieService.searchMovies(name, id, genre);
        
        // Add search results and metadata to model
        model.addAttribute("movies", searchResults);
        model.addAttribute("searchPerformed", true);
        model.addAttribute("searchName", name);
        model.addAttribute("searchId", idParam);
        model.addAttribute("searchGenre", genre);
        
        // Add pirate-themed messages based on results
        if (searchResults.isEmpty()) {
            model.addAttribute("emptyResultsMessage", 
                "Blimey! No treasures found matching yer search criteria, matey! " +
                "Try adjusting yer search terms or sail back to view all movies.");
        } else {
            String resultMessage = searchResults.size() == 1 ? 
                "Ahoy! Found 1 treasure matching yer search, ye savvy sailor!" :
                String.format("Shiver me timbers! Found %d treasures matching yer search criteria!", searchResults.size());
            model.addAttribute("successMessage", resultMessage);
        }
        
        logger.info("Search completed successfully with {} results", searchResults.size());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }
}
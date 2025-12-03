package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MockMovieService mockMovieService;
    private ReviewService mockReviewService;

    // Mock MovieService for testing
    private class MockMovieService extends MovieService {
        private List<Movie> testMovies;
        
        public MockMovieService() {
            testMovies = Arrays.asList(
                new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                new Movie(3L, "Comedy Film", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
            );
        }
        
        @Override
        public List<Movie> getAllMovies() {
            return testMovies;
        }
        
        @Override
        public Optional<Movie> getMovieById(Long id) {
            return testMovies.stream().filter(m -> m.getId().equals(id)).findFirst();
        }
        
        @Override
        public List<Movie> searchMovies(String name, Long id, String genre) {
            return testMovies.stream()
                .filter(movie -> name == null || movie.getMovieName().toLowerCase().contains(name.toLowerCase()))
                .filter(movie -> id == null || movie.getId().equals(id))
                .filter(movie -> genre == null || movie.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        @Override
        public String validateSearchParameters(String name, String id, String genre) {
            if ((name == null || name.trim().isEmpty()) && 
                (id == null || id.trim().isEmpty()) && 
                (genre == null || genre.trim().isEmpty())) {
                return "Avast ye! Ye need to provide at least one search criterion, matey!";
            }
            
            if (id != null && !id.trim().isEmpty()) {
                try {
                    Long idValue = Long.parseLong(id.trim());
                    if (idValue <= 0) {
                        return "Blimey! Movie ID must be a positive number, ye scallywag!";
                    }
                } catch (NumberFormatException e) {
                    return "Arrr! That be not a valid movie ID, ye landlubber!";
                }
            }
            
            return null;
        }
    }

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MockMovieService();
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Should return movies view for GET /movies")
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertNotNull(movies);
        assertEquals(3, movies.size());
    }

    @Test
    @DisplayName("Should return movie details for valid movie ID")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
        
        Movie movie = (Movie) model.asMap().get("movie");
        assertNotNull(movie);
        assertEquals("Test Movie", movie.getMovieName());
    }

    @Test
    @DisplayName("Should return error view for invalid movie ID")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
        
        String title = (String) model.asMap().get("title");
        assertEquals("Movie Not Found", title);
    }

    @Test
    @DisplayName("Should search movies by name successfully")
    public void testSearchMoviesByName() {
        String result = moviesController.searchMovies("Test", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        assertTrue((Boolean) model.asMap().get("searchPerformed"));
        assertEquals("Test", model.asMap().get("searchName"));
        assertNotNull(model.asMap().get("successMessage"));
    }

    @Test
    @DisplayName("Should search movies by ID successfully")
    public void testSearchMoviesById() {
        String result = moviesController.searchMovies(null, "2", null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Movie", movies.get(0).getMovieName());
        
        assertTrue((Boolean) model.asMap().get("searchPerformed"));
        assertEquals("2", model.asMap().get("searchId"));
    }

    @Test
    @DisplayName("Should search movies by genre successfully")
    public void testSearchMoviesByGenre() {
        String result = moviesController.searchMovies(null, null, "Comedy", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertEquals(1, movies.size());
        assertEquals("Comedy Film", movies.get(0).getMovieName());
        
        assertTrue((Boolean) model.asMap().get("searchPerformed"));
        assertEquals("Comedy", model.asMap().get("searchGenre"));
    }

    @Test
    @DisplayName("Should return empty results with appropriate message")
    public void testSearchMoviesNoResults() {
        String result = moviesController.searchMovies("NonExistent", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertTrue(movies.isEmpty());
        
        assertTrue((Boolean) model.asMap().get("searchPerformed"));
        assertNotNull(model.asMap().get("emptyResultsMessage"));
        String emptyMessage = (String) model.asMap().get("emptyResultsMessage");
        assertTrue(emptyMessage.contains("Blimey"));
    }

    @Test
    @DisplayName("Should handle validation errors with pirate messages")
    public void testSearchMoviesValidationError() {
        // Test empty parameters
        String result = moviesController.searchMovies("", "", "", model);
        assertEquals("movies", result);
        
        String errorMessage = (String) model.asMap().get("errorMessage");
        assertNotNull(errorMessage);
        assertTrue(errorMessage.contains("Avast ye"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertEquals(3, movies.size()); // Should return all movies on error
    }

    @Test
    @DisplayName("Should handle invalid ID parameter")
    public void testSearchMoviesInvalidId() {
        String result = moviesController.searchMovies(null, "abc", null, model);
        assertEquals("movies", result);
        
        String errorMessage = (String) model.asMap().get("errorMessage");
        assertNotNull(errorMessage);
        assertTrue(errorMessage.contains("landlubber"));
    }

    @Test
    @DisplayName("Should preserve search parameters in model")
    public void testSearchParametersPreservation() {
        String result = moviesController.searchMovies("Test", "1", "Drama", model);
        assertEquals("movies", result);
        
        assertEquals("Test", model.asMap().get("searchName"));
        assertEquals("1", model.asMap().get("searchId"));
        assertEquals("Drama", model.asMap().get("searchGenre"));
        assertTrue((Boolean) model.asMap().get("searchPerformed"));
    }

    @Test
    @DisplayName("Should generate appropriate success messages")
    public void testSuccessMessages() {
        // Test single result message
        String result = moviesController.searchMovies("Test", null, null, model);
        String successMessage = (String) model.asMap().get("successMessage");
        assertTrue(successMessage.contains("Found 1 treasure"));
        assertTrue(successMessage.contains("savvy sailor"));
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }
}

package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should return all movies when no search criteria provided")
    public void testSearchMoviesWithNoCriteria() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertEquals(12, results.size(), "Should return all 12 movies when no criteria provided");
    }

    @Test
    @DisplayName("Should search movies by name (case-insensitive partial match)")
    public void testSearchMoviesByName() {
        // Test partial match
        List<Movie> results = movieService.searchMovies("prison", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Test case insensitive
        results = movieService.searchMovies("FAMILY", null, null);
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());

        // Test no matches
        results = movieService.searchMovies("nonexistent", null, null);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should search movies by ID (exact match)")
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Test non-existent ID
        results = movieService.searchMovies(null, 999L, null);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should search movies by genre (case-insensitive partial match)")
    public void testSearchMoviesByGenre() {
        // Test partial match for Drama
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertTrue(results.size() >= 3, "Should find multiple drama movies");

        // Test case insensitive
        results = movieService.searchMovies(null, null, "action");
        assertTrue(results.size() >= 2, "Should find action movies");

        // Test compound genre
        results = movieService.searchMovies(null, null, "Crime");
        assertTrue(results.size() >= 3, "Should find crime movies");
    }

    @Test
    @DisplayName("Should search movies with multiple criteria (AND logic)")
    public void testSearchMoviesWithMultipleCriteria() {
        // Search for Drama movies with "The" in the name
        List<Movie> results = movieService.searchMovies("The", null, "Drama");
        assertTrue(results.size() >= 1, "Should find drama movies with 'The' in name");

        // Search for specific movie by name and ID
        results = movieService.searchMovies("Prison", 1L, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Search with conflicting criteria (should return empty)
        results = movieService.searchMovies("Prison", 2L, null);
        assertTrue(results.isEmpty(), "Should return empty for conflicting criteria");
    }

    @Test
    @DisplayName("Should search movies by name only")
    public void testSearchMoviesByNameOnly() {
        List<Movie> results = movieService.searchMoviesByName("The");
        assertTrue(results.size() >= 5, "Should find multiple movies with 'The' in name");

        results = movieService.searchMoviesByName("Space");
        assertEquals(1, results.size());
        assertEquals("Space Wars: The Beginning", results.get(0).getMovieName());

        // Test empty/null name
        results = movieService.searchMoviesByName("");
        assertEquals(12, results.size(), "Should return all movies for empty name");

        results = movieService.searchMoviesByName(null);
        assertEquals(12, results.size(), "Should return all movies for null name");
    }

    @Test
    @DisplayName("Should search movies by genre only")
    public void testSearchMoviesByGenreOnly() {
        List<Movie> results = movieService.searchMoviesByGenre("Sci-Fi");
        assertTrue(results.size() >= 2, "Should find sci-fi movies");

        results = movieService.searchMoviesByGenre("Fantasy");
        assertEquals(1, results.size());
        assertEquals("The Quest for the Ring", results.get(0).getMovieName());

        // Test empty/null genre
        results = movieService.searchMoviesByGenre("");
        assertEquals(12, results.size(), "Should return all movies for empty genre");

        results = movieService.searchMoviesByGenre(null);
        assertEquals(12, results.size(), "Should return all movies for null genre");
    }

    @Test
    @DisplayName("Should validate search parameters correctly")
    public void testValidateSearchParameters() {
        // Valid parameters
        assertNull(movieService.validateSearchParameters("test", "1", "drama"));
        assertNull(movieService.validateSearchParameters("test", null, null));
        assertNull(movieService.validateSearchParameters(null, "5", null));
        assertNull(movieService.validateSearchParameters(null, null, "action"));

        // All empty parameters
        String error = movieService.validateSearchParameters("", "", "");
        assertNotNull(error);
        assertTrue(error.contains("Avast ye"));

        error = movieService.validateSearchParameters(null, null, null);
        assertNotNull(error);
        assertTrue(error.contains("Avast ye"));

        // Invalid ID
        error = movieService.validateSearchParameters(null, "abc", null);
        assertNotNull(error);
        assertTrue(error.contains("landlubber"));

        error = movieService.validateSearchParameters(null, "-1", null);
        assertNotNull(error);
        assertTrue(error.contains("scallywag"));

        // Name too long
        String longName = "a".repeat(101);
        error = movieService.validateSearchParameters(longName, null, null);
        assertNotNull(error);
        assertTrue(error.contains("treasure map"));

        // Genre too long
        String longGenre = "a".repeat(51);
        error = movieService.validateSearchParameters(null, null, longGenre);
        assertNotNull(error);
        assertTrue(error.contains("ship's log"));
    }

    @Test
    @DisplayName("Should handle edge cases gracefully")
    public void testEdgeCases() {
        // Whitespace handling
        List<Movie> results = movieService.searchMovies("  The  ", null, null);
        assertTrue(results.size() >= 5, "Should handle whitespace in search terms");

        results = movieService.searchMovies(null, null, "  Drama  ");
        assertTrue(results.size() >= 3, "Should handle whitespace in genre");

        // Special characters
        results = movieService.searchMovies("The-Prison", null, null);
        assertTrue(results.isEmpty(), "Should not match special characters");

        // Case sensitivity
        results = movieService.searchMovies("the prison escape", null, null);
        assertEquals(1, results.size(), "Should be case insensitive");
    }

    @Test
    @DisplayName("Should maintain existing functionality")
    public void testExistingFunctionality() {
        // Test getAllMovies
        List<Movie> allMovies = movieService.getAllMovies();
        assertEquals(12, allMovies.size());

        // Test getMovieById
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals("The Prison Escape", movie.get().getMovieName());

        // Test invalid ID
        movie = movieService.getMovieById(-1L);
        assertFalse(movie.isPresent());

        movie = movieService.getMovieById(null);
        assertFalse(movie.isPresent());
    }
}
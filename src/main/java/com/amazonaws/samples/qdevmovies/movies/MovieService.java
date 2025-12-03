package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Search movies by multiple criteria with pirate-themed logging
     * @param name Movie name (partial match, case-insensitive)
     * @param id Movie ID (exact match)
     * @param genre Movie genre (partial match, case-insensitive)
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Ahoy! Searching the treasure chest for movies with criteria - name: '{}', id: {}, genre: '{}'", 
                   name, id, genre);
        
        List<Movie> results = movies.stream()
            .filter(movie -> matchesName(movie, name))
            .filter(movie -> matchesId(movie, id))
            .filter(movie -> matchesGenre(movie, genre))
            .collect(Collectors.toList());
        
        logger.info("Arrr! Found {} treasures matching yer search criteria, matey!", results.size());
        return results;
    }

    /**
     * Search movies by name only (partial match, case-insensitive)
     * @param name Movie name to search for
     * @return List of movies with names containing the search term
     */
    public List<Movie> searchMoviesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Blimey! Empty name provided for search, returning all movies");
            return new ArrayList<>(movies);
        }
        
        String searchTerm = name.trim().toLowerCase();
        logger.info("Searching for movies with name containing: '{}'", searchTerm);
        
        return movies.stream()
            .filter(movie -> movie.getMovieName().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
    }

    /**
     * Search movies by genre only (partial match, case-insensitive)
     * @param genre Movie genre to search for
     * @return List of movies with genres containing the search term
     */
    public List<Movie> searchMoviesByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            logger.warn("Shiver me timbers! Empty genre provided for search, returning all movies");
            return new ArrayList<>(movies);
        }
        
        String searchTerm = genre.trim().toLowerCase();
        logger.info("Searching for movies with genre containing: '{}'", searchTerm);
        
        return movies.stream()
            .filter(movie -> movie.getGenre().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
    }

    private boolean matchesName(Movie movie, String name) {
        if (name == null || name.trim().isEmpty()) {
            return true; // No name filter applied
        }
        return movie.getMovieName().toLowerCase().contains(name.trim().toLowerCase());
    }

    private boolean matchesId(Movie movie, Long id) {
        if (id == null || id <= 0) {
            return true; // No ID filter applied
        }
        return movie.getId() == id;
    }

    private boolean matchesGenre(Movie movie, String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return true; // No genre filter applied
        }
        return movie.getGenre().toLowerCase().contains(genre.trim().toLowerCase());
    }

    /**
     * Validate search parameters and return validation messages with pirate flair
     * @param name Movie name parameter
     * @param id Movie ID parameter
     * @param genre Movie genre parameter
     * @return Validation message or null if all parameters are valid
     */
    public String validateSearchParameters(String name, String id, String genre) {
        // Check if all parameters are empty
        if ((name == null || name.trim().isEmpty()) && 
            (id == null || id.trim().isEmpty()) && 
            (genre == null || genre.trim().isEmpty())) {
            return "Avast ye! Ye need to provide at least one search criterion, matey!";
        }

        // Validate ID parameter if provided
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

        // Validate name length if provided
        if (name != null && name.trim().length() > 100) {
            return "Shiver me timbers! Movie name be too long for our treasure map!";
        }

        // Validate genre length if provided
        if (genre != null && genre.trim().length() > 50) {
            return "Batten down the hatches! Genre name be too long for our ship's log!";
        }

        return null; // All parameters are valid
    }
}

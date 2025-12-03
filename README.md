# Movie Service - Spring Boot Demo Application

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a fun pirate-themed search interface.

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **🏴‍☠️ Pirate-Themed Movie Search**: Search the treasure chest of movies by name, ID, or genre with nautical flair
- **Advanced Filtering**: Combine multiple search criteria to find exactly what ye be lookin' for
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**
- **Thymeleaf** for templating

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Movie Search**: http://localhost:8080/movies/search (with query parameters)

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller for movie endpoints
│   │       │   ├── MovieService.java         # Business logic with search functionality
│   │       │   ├── Movie.java                # Movie data model
│   │       │   └── Review.java               # Review data model
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data (12 movies)
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       └── templates/
│           ├── movies.html                   # Main movie list with search form
│           └── movie-details.html            # Movie details page
└── test/                                     # Comprehensive unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MovieServiceTest.java         # Service layer tests
            └── MoviesControllerTest.java     # Controller tests
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings, basic information, and a pirate-themed search form.

### Search Movies 🏴‍☠️
```
GET /movies/search
```
Search the treasure chest of movies using pirate-themed interface with multiple criteria.

**Query Parameters:**
- `name` (optional): Movie name (partial match, case-insensitive)
- `id` (optional): Movie ID (exact match, 1-12)
- `genre` (optional): Movie genre (partial match, case-insensitive)

**Examples:**
```bash
# Search by movie name
http://localhost:8080/movies/search?name=prison

# Search by movie ID
http://localhost:8080/movies/search?id=1

# Search by genre
http://localhost:8080/movies/search?genre=drama

# Combine multiple criteria (AND logic)
http://localhost:8080/movies/search?name=the&genre=action

# Search for sci-fi movies
http://localhost:8080/movies/search?genre=sci-fi
```

**Response Features:**
- Pirate-themed success and error messages
- Maintains search parameters in form after submission
- Displays result count with nautical terminology
- Handles empty results with encouraging pirate messages
- Validates input parameters with humorous error messages

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## Search Functionality

### Available Movies and Genres
The application includes 12 movies across various genres:
- **Drama**: The Prison Escape, Life Journey, The Factory Owner
- **Crime/Drama**: The Family Boss, Urban Stories, The Wise Guys
- **Action/Crime**: The Masked Hero
- **Action/Sci-Fi**: Dream Heist, The Virtual World
- **Adventure/Fantasy**: The Quest for the Ring
- **Adventure/Sci-Fi**: Space Wars: The Beginning
- **Drama/Thriller**: Underground Club

### Search Tips 🗺️
- **Name searches** are case-insensitive and match partial strings
- **Genre searches** work with partial matches (e.g., "sci" finds "Sci-Fi")
- **Multiple criteria** use AND logic (all must match)
- **Empty searches** return validation errors with pirate flair
- **Invalid IDs** trigger humorous error messages

### Pirate Language Features
The search interface includes authentic pirate terminology:
- **Success messages**: "Ahoy! Found X treasures matching yer search!"
- **Error messages**: "Avast ye! Ye need to provide at least one search criterion, matey!"
- **Empty results**: "Blimey! No treasures found matching yer criteria!"
- **Form labels**: Uses nautical terms and pirate expressions
- **Buttons**: "⚔️ Search Treasures!" and "🧭 Show All Movies"

## Testing

### Run Unit Tests
```bash
mvn test
```

### Test Coverage
The application includes comprehensive unit tests covering:
- **MovieService**: Search functionality, parameter validation, edge cases
- **MoviesController**: Search endpoint, parameter handling, pirate messaging
- **Integration**: End-to-end search scenarios with various parameter combinations

### Test Categories
- Search by single criteria (name, ID, genre)
- Multi-criteria searches with AND logic
- Parameter validation and error handling
- Pirate language integration
- Edge cases (empty results, invalid input, whitespace handling)
- Backward compatibility with existing functionality

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check that all parameters are properly URL-encoded
2. Verify movie IDs are between 1-12
3. Check application logs for detailed error messages
4. Ensure at least one search parameter is provided

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the UI/UX with more pirate themes
- Add new search features (year range, rating filters)
- Improve the responsive design
- Extend the pirate language vocabulary
- Add more comprehensive error handling

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

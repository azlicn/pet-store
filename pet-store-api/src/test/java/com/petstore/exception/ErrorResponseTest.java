package com.petstore.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ErrorResponse.
 * Tests constructors, getters, setters, and field behavior.
 */
@DisplayName("ErrorResponse Tests")
class ErrorResponseTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create ErrorResponse with default constructor")
        void testDefaultConstructor() {
            // When
            ErrorResponse response = new ErrorResponse();

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getTimestamp()).isNotNull();
            assertThat(response.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
            assertThat(response.getTimestamp()).isAfter(LocalDateTime.now().minusSeconds(1));
        }

        @Test
        @DisplayName("Should create ErrorResponse with all parameters")
        void testParameterizedConstructor() {
            // Given
            int status = 404;
            String error = "Not Found";
            String message = "Resource not found";
            String path = "/api/pets/123";
            String code = "PET_NOT_FOUND";

            // When
            ErrorResponse response = new ErrorResponse(status, error, message, path, code);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getStatus()).isEqualTo(404);
            assertThat(response.getError()).isEqualTo("Not Found");
            assertThat(response.getMessage()).isEqualTo("Resource not found");
            assertThat(response.getPath()).isEqualTo("/api/pets/123");
            assertThat(response.getCode()).isEqualTo("PET_NOT_FOUND");
            assertThat(response.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Should set timestamp automatically in parameterized constructor")
        void testParameterizedConstructorSetsTimestamp() {
            // Given
            LocalDateTime before = LocalDateTime.now();

            // When
            ErrorResponse response = new ErrorResponse(400, "Bad Request", "Invalid input", "/api/test", "VALIDATION_ERROR");
            LocalDateTime after = LocalDateTime.now();

            // Then
            assertThat(response.getTimestamp()).isNotNull();
            assertThat(response.getTimestamp()).isAfterOrEqualTo(before);
            assertThat(response.getTimestamp()).isBeforeOrEqualTo(after);
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set timestamp")
        void testTimestampGetterSetter() {
            // Given
            ErrorResponse response = new ErrorResponse();
            LocalDateTime customTimestamp = LocalDateTime.of(2025, 11, 1, 10, 30, 0);

            // When
            response.setTimestamp(customTimestamp);

            // Then
            assertThat(response.getTimestamp()).isEqualTo(customTimestamp);
        }

        @Test
        @DisplayName("Should get and set status")
        void testStatusGetterSetter() {
            // Given
            ErrorResponse response = new ErrorResponse();

            // When
            response.setStatus(500);

            // Then
            assertThat(response.getStatus()).isEqualTo(500);
        }

        @Test
        @DisplayName("Should get and set error")
        void testErrorGetterSetter() {
            // Given
            ErrorResponse response = new ErrorResponse();

            // When
            response.setError("Internal Server Error");

            // Then
            assertThat(response.getError()).isEqualTo("Internal Server Error");
        }

        @Test
        @DisplayName("Should get and set message")
        void testMessageGetterSetter() {
            // Given
            ErrorResponse response = new ErrorResponse();

            // When
            response.setMessage("An unexpected error occurred");

            // Then
            assertThat(response.getMessage()).isEqualTo("An unexpected error occurred");
        }

        @Test
        @DisplayName("Should get and set path")
        void testPathGetterSetter() {
            // Given
            ErrorResponse response = new ErrorResponse();

            // When
            response.setPath("/api/users/42");

            // Then
            assertThat(response.getPath()).isEqualTo("/api/users/42");
        }

        @Test
        @DisplayName("Should get and set code")
        void testCodeGetterSetter() {
            // Given
            ErrorResponse response = new ErrorResponse();

            // When
            response.setCode("USER_NOT_FOUND");

            // Then
            assertThat(response.getCode()).isEqualTo("USER_NOT_FOUND");
        }
    }

    @Nested
    @DisplayName("Field Value Tests")
    class FieldValueTests {

        @Test
        @DisplayName("Should handle various HTTP status codes")
        void testVariousStatusCodes() {
            // Given & When
            ErrorResponse response1 = new ErrorResponse(200, "OK", "Success", "/api/test", "SUCCESS");
            ErrorResponse response2 = new ErrorResponse(400, "Bad Request", "Invalid", "/api/test", "BAD_REQUEST");
            ErrorResponse response3 = new ErrorResponse(401, "Unauthorized", "Auth required", "/api/test", "UNAUTHORIZED");
            ErrorResponse response4 = new ErrorResponse(403, "Forbidden", "Access denied", "/api/test", "FORBIDDEN");
            ErrorResponse response5 = new ErrorResponse(404, "Not Found", "Missing", "/api/test", "NOT_FOUND");
            ErrorResponse response6 = new ErrorResponse(500, "Internal Error", "Server error", "/api/test", "SERVER_ERROR");

            // Then
            assertThat(response1.getStatus()).isEqualTo(200);
            assertThat(response2.getStatus()).isEqualTo(400);
            assertThat(response3.getStatus()).isEqualTo(401);
            assertThat(response4.getStatus()).isEqualTo(403);
            assertThat(response5.getStatus()).isEqualTo(404);
            assertThat(response6.getStatus()).isEqualTo(500);
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void testNullValues() {
            // Given
            ErrorResponse response = new ErrorResponse(404, "Not Found", "Message", "/path", "CODE");

            // When
            response.setError(null);
            response.setMessage(null);
            response.setPath(null);
            response.setCode(null);
            response.setTimestamp(null);

            // Then
            assertThat(response.getError()).isNull();
            assertThat(response.getMessage()).isNull();
            assertThat(response.getPath()).isNull();
            assertThat(response.getCode()).isNull();
            assertThat(response.getTimestamp()).isNull();
        }

        @Test
        @DisplayName("Should handle empty strings")
        void testEmptyStrings() {
            // Given
            ErrorResponse response = new ErrorResponse();

            // When
            response.setError("");
            response.setMessage("");
            response.setPath("");
            response.setCode("");

            // Then
            assertThat(response.getError()).isEmpty();
            assertThat(response.getMessage()).isEmpty();
            assertThat(response.getPath()).isEmpty();
            assertThat(response.getCode()).isEmpty();
        }

        @Test
        @DisplayName("Should handle long error messages")
        void testLongMessages() {
            // Given
            ErrorResponse response = new ErrorResponse();
            String longMessage = "This is a very long error message that contains detailed information " +
                    "about what went wrong in the system. It includes multiple sentences and provides " +
                    "comprehensive details to help debug the issue. The message can be as long as needed " +
                    "to provide sufficient context for troubleshooting.";

            // When
            response.setMessage(longMessage);

            // Then
            assertThat(response.getMessage()).isEqualTo(longMessage);
            assertThat(response.getMessage().length()).isGreaterThan(100);
        }

        @Test
        @DisplayName("Should handle special characters in strings")
        void testSpecialCharacters() {
            // Given
            ErrorResponse response = new ErrorResponse();

            // When
            response.setError("Error: <script>alert('XSS')</script>");
            response.setMessage("Message with \"quotes\" and 'apostrophes'");
            response.setPath("/api/test?param=value&other=123");
            response.setCode("ERROR_CODE_WITH_UNDERSCORES");

            // Then
            assertThat(response.getError()).contains("<script>");
            assertThat(response.getMessage()).contains("\"quotes\"");
            assertThat(response.getPath()).contains("?");
            assertThat(response.getCode()).contains("_");
        }
    }

    @Nested
    @DisplayName("Complete Object Tests")
    class CompleteObjectTests {

        @Test
        @DisplayName("Should create complete error response for 404 Not Found")
        void testCompleteNotFoundResponse() {
            // When
            ErrorResponse response = new ErrorResponse(
                    404,
                    "Not Found",
                    "Pet with id 123 not found",
                    "/api/pets/123",
                    ErrorCodes.PET_NOT_FOUND
            );

            // Then
            assertThat(response.getStatus()).isEqualTo(404);
            assertThat(response.getError()).isEqualTo("Not Found");
            assertThat(response.getMessage()).isEqualTo("Pet with id 123 not found");
            assertThat(response.getPath()).isEqualTo("/api/pets/123");
            assertThat(response.getCode()).isEqualTo(ErrorCodes.PET_NOT_FOUND);
            assertThat(response.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Should create complete error response for 400 Bad Request")
        void testCompleteBadRequestResponse() {
            // When
            ErrorResponse response = new ErrorResponse(
                    400,
                    "Bad Request",
                    "Invalid input: email is required",
                    "/api/users",
                    ErrorCodes.VALIDATION_FAILED
            );

            // Then
            assertThat(response.getStatus()).isEqualTo(400);
            assertThat(response.getError()).isEqualTo("Bad Request");
            assertThat(response.getMessage()).contains("email is required");
            assertThat(response.getPath()).isEqualTo("/api/users");
            assertThat(response.getCode()).isEqualTo(ErrorCodes.VALIDATION_FAILED);
        }

        @Test
        @DisplayName("Should create complete error response for 500 Internal Server Error")
        void testCompleteInternalServerErrorResponse() {
            // When
            ErrorResponse response = new ErrorResponse(
                    500,
                    "Internal Server Error",
                    "An unexpected error occurred. Please try again later.",
                    "/api/orders",
                    ErrorCodes.INTERNAL_SERVER_ERROR
            );

            // Then
            assertThat(response.getStatus()).isEqualTo(500);
            assertThat(response.getError()).isEqualTo("Internal Server Error");
            assertThat(response.getMessage()).isEqualTo("An unexpected error occurred. Please try again later.");
            assertThat(response.getPath()).isEqualTo("/api/orders");
            assertThat(response.getCode()).isEqualTo(ErrorCodes.INTERNAL_SERVER_ERROR);
        }

        @Test
        @DisplayName("Should allow modification of all fields after creation")
        void testFieldModification() {
            // Given
            ErrorResponse response = new ErrorResponse(
                    404,
                    "Not Found",
                    "Original message",
                    "/api/original",
                    "ORIGINAL_CODE"
            );

            // When
            response.setStatus(400);
            response.setError("Bad Request");
            response.setMessage("Modified message");
            response.setPath("/api/modified");
            response.setCode("MODIFIED_CODE");
            LocalDateTime newTimestamp = LocalDateTime.of(2025, 1, 1, 12, 0, 0);
            response.setTimestamp(newTimestamp);

            // Then
            assertThat(response.getStatus()).isEqualTo(400);
            assertThat(response.getError()).isEqualTo("Bad Request");
            assertThat(response.getMessage()).isEqualTo("Modified message");
            assertThat(response.getPath()).isEqualTo("/api/modified");
            assertThat(response.getCode()).isEqualTo("MODIFIED_CODE");
            assertThat(response.getTimestamp()).isEqualTo(newTimestamp);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle zero status code")
        void testZeroStatusCode() {
            // When
            ErrorResponse response = new ErrorResponse(0, "Unknown", "Test", "/test", "TEST");

            // Then
            assertThat(response.getStatus()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should handle negative status code")
        void testNegativeStatusCode() {
            // When
            ErrorResponse response = new ErrorResponse(-1, "Invalid", "Test", "/test", "TEST");

            // Then
            assertThat(response.getStatus()).isEqualTo(-1);
        }

        @Test
        @DisplayName("Should handle very large status code")
        void testLargeStatusCode() {
            // When
            ErrorResponse response = new ErrorResponse(999, "Unknown", "Test", "/test", "TEST");

            // Then
            assertThat(response.getStatus()).isEqualTo(999);
        }

        @Test
        @DisplayName("Should preserve whitespace in strings")
        void testWhitespacePreservation() {
            // Given
            ErrorResponse response = new ErrorResponse();

            // When
            response.setError("  Error with spaces  ");
            response.setMessage("\tMessage with tab\n");
            response.setPath("  /api/path  ");

            // Then
            assertThat(response.getError()).isEqualTo("  Error with spaces  ");
            assertThat(response.getMessage()).isEqualTo("\tMessage with tab\n");
            assertThat(response.getPath()).isEqualTo("  /api/path  ");
        }

        @Test
        @DisplayName("Should handle unicode characters")
        void testUnicodeCharacters() {
            // Given
            ErrorResponse response = new ErrorResponse();

            // When
            response.setError("错误");
            response.setMessage("Fehler: Ungültige Eingabe");
            response.setCode("エラーコード");

            // Then
            assertThat(response.getError()).isEqualTo("错误");
            assertThat(response.getMessage()).contains("Ungültige");
            assertThat(response.getCode()).isEqualTo("エラーコード");
        }
    }

    @Nested
    @DisplayName("Timestamp Behavior Tests")
    class TimestampBehaviorTests {

        @Test
        @DisplayName("Should have timestamp close to current time for default constructor")
        void testDefaultConstructorTimestamp() {
            // Given
            LocalDateTime before = LocalDateTime.now();

            // When
            ErrorResponse response = new ErrorResponse();

            // Then
            LocalDateTime after = LocalDateTime.now();
            assertThat(response.getTimestamp()).isNotNull();
            assertThat(response.getTimestamp()).isBetween(before, after);
        }

        @Test
        @DisplayName("Should have timestamp close to current time for parameterized constructor")
        void testParameterizedConstructorTimestamp() {
            // Given
            LocalDateTime before = LocalDateTime.now();

            // When
            ErrorResponse response = new ErrorResponse(404, "Not Found", "Message", "/path", "CODE");

            // Then
            LocalDateTime after = LocalDateTime.now();
            assertThat(response.getTimestamp()).isNotNull();
            assertThat(response.getTimestamp()).isBetween(before, after);
        }

        @Test
        @DisplayName("Should allow setting past timestamp")
        void testPastTimestamp() {
            // Given
            ErrorResponse response = new ErrorResponse();
            LocalDateTime pastTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

            // When
            response.setTimestamp(pastTime);

            // Then
            assertThat(response.getTimestamp()).isEqualTo(pastTime);
            assertThat(response.getTimestamp()).isBefore(LocalDateTime.now());
        }

        @Test
        @DisplayName("Should allow setting future timestamp")
        void testFutureTimestamp() {
            // Given
            ErrorResponse response = new ErrorResponse();
            LocalDateTime futureTime = LocalDateTime.of(2030, 12, 31, 23, 59, 59);

            // When
            response.setTimestamp(futureTime);

            // Then
            assertThat(response.getTimestamp()).isEqualTo(futureTime);
            assertThat(response.getTimestamp()).isAfter(LocalDateTime.now());
        }
    }
}

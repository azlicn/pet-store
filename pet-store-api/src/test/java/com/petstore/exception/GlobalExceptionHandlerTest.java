package com.petstore.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Unit tests for GlobalExceptionHandler.
 * Tests exception handling logic and error response generation.
 */
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Nested
    @DisplayName("Payment Exception Tests")
    class PaymentExceptionTests {

        @Test
        @DisplayName("Should handle InvalidDiscountException with BAD_REQUEST")
        void testHandleInvalidDiscountException() {
            // Given
            InvalidDiscountException exception = new InvalidDiscountException("Invalid discount code");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidDiscountException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getError()).isEqualTo("Invalid Discount Code");
            assertThat(response.getBody().getMessage()).isEqualTo("Invalid discount code");
            assertThat(response.getBody().getPath()).isEqualTo("/api/test");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INVALID_DISCOUNT_CODE);
        }

        @Test
        @DisplayName("Should handle InvalidPaymentException with BAD_REQUEST")
        void testHandleInvalidPaymentException() {
            // Given
            InvalidPaymentException exception = new InvalidPaymentException("Invalid payment details");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidPaymentException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Invalid Payment");
            assertThat(response.getBody().getMessage()).isEqualTo("Invalid payment details");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INVALID_PAYMENT);
        }

        @Test
        @DisplayName("Should handle UnsupportedPaymentException with BAD_REQUEST")
        void testHandleUnsupportedPaymentException() {
            // Given
            UnsupportedPaymentException exception = new UnsupportedPaymentException("Payment method not supported");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnsupportedPaymentException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Unsupported Payment");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.UNSUPPORTED_PAYMENT);
        }

        @Test
        @DisplayName("Should handle UnsupportedPaymentTypeException with BAD_REQUEST")
        void testHandleUnsupportedPaymentTypeException() {
            // Given
            UnsupportedPaymentTypeException exception = new UnsupportedPaymentTypeException("Payment type not supported");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnsupportedPaymentTypeException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Unsupported Payment Type");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.UNSUPPORTED_PAYMENT_TYPE);
        }
    }

    @Nested
    @DisplayName("Address Exception Tests")
    class AddressExceptionTests {

        @Test
        @DisplayName("Should handle AddressNotFoundException with NOT_FOUND")
        void testHandleAddressNotFoundException() {
            // Given
            AddressNotFoundException exception = new AddressNotFoundException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleAddressNotFoundException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(404);
            assertThat(response.getBody().getError()).isEqualTo("Address Not Found");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.ADDRESS_NOT_FOUND);
        }

        @Test
        @DisplayName("Should handle AddressInUseException with CONFLICT")
        void testHandleAddressInUseException() {
            // Given
            AddressInUseException exception = new AddressInUseException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleAddressInUseException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(409);
            assertThat(response.getBody().getError()).isEqualTo("Address In Use");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.ADDRESS_IN_USE);
        }
    }

    @Nested
    @DisplayName("Cart Exception Tests")
    class CartExceptionTests {

        @Test
        @DisplayName("Should handle CartItemNotFoundException with NOT_FOUND")
        void testHandleCartItemNotFoundException() {
            // Given
            CartItemNotFoundException exception = new CartItemNotFoundException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleCartItemNotFoundException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Cart Item Not Found");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.CART_ITEM_NOT_FOUND);
        }

        @Test
        @DisplayName("Should handle CartEmptyException with BAD_REQUEST")
        void testHandleCartEmptyException() {
            // Given
            CartEmptyException exception = new CartEmptyException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleCartEmptyException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Cart Is Empty");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.CART_IS_EMPTY);
        }

        @Test
        @DisplayName("Should handle UserCartNotFoundException with NOT_FOUND")
        void testHandleUserCartNotFoundException() {
            // Given
            UserCartNotFoundException exception = new UserCartNotFoundException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserCartNotFoundException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("User Cart Not Found");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.USER_CART_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Order Exception Tests")
    class OrderExceptionTests {

        @Test
        @DisplayName("Should handle OrderNotFoundException with NOT_FOUND")
        void testHandleOrderNotFoundException() {
            // Given
            OrderNotFoundException exception = new OrderNotFoundException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleOrderNotFoundException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Order Not Found");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.ORDER_NOT_FOUND);
        }

        @Test
        @DisplayName("Should handle OrderOwnershipException with FORBIDDEN")
        void testHandleOrderOwnershipException() {
            // Given
            OrderOwnershipException exception = new OrderOwnershipException(1L, 2L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleOrderOwnershipException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(403);
            assertThat(response.getBody().getError()).isEqualTo("Order Access Denied");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.ORDER_ACCESS_DENIED);
        }
    }

    @Nested
    @DisplayName("Pet Exception Tests")
    class PetExceptionTests {

        @Test
        @DisplayName("Should handle PetNotFoundException with NOT_FOUND")
        void testHandlePetNotFoundException() {
            // Given
            PetNotFoundException exception = new PetNotFoundException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handlePetNotFoundException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Pet Not Found");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.PET_NOT_FOUND);
        }

        @Test
        @DisplayName("Should handle PetAlreadySoldException with CONFLICT")
        void testHandlePetAlreadySoldException() {
            // Given
            PetAlreadySoldException exception = new PetAlreadySoldException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handlePetAlreadySoldException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Pet Already Sold");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.PET_ALREADY_SOLD);
        }

        @Test
        @DisplayName("Should handle PetAlreadyExistInUserCartException with CONFLICT")
        void testHandlePetAlreadyExistInUserCartException() {
            // Given
            PetAlreadyExistInUserCartException exception = new PetAlreadyExistInUserCartException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handlePetAlreadyExistInUserCartException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Pet Already Exists in User Cart");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.PET_ALREADY_EXISTS_IN_USER_CART);
        }

        @Test
        @DisplayName("Should handle InvalidPetException with BAD_REQUEST")
        void testHandleInvalidPetException() {
            // Given
            InvalidPetException exception = new InvalidPetException("Invalid pet data");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidPetException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Invalid Pet");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INVALID_PET);
        }
    }

    @Nested
    @DisplayName("User Exception Tests")
    class UserExceptionTests {

        @Test
        @DisplayName("Should handle UserNotFoundException with NOT_FOUND")
        void testHandleUserNotFoundException() {
            // Given
            UserNotFoundException exception = new UserNotFoundException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("User Not Found");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("Should handle InvalidUserException with BAD_REQUEST")
        void testHandleInvalidUserException() {
            // Given
            InvalidUserException exception = new InvalidUserException("Invalid user data");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidUserException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Invalid User");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INVALID_USER);
        }

        @Test
        @DisplayName("Should handle UserInUseException with CONFLICT")
        void testHandleUserInUseException() {
            // Given
            UserInUseException exception = new UserInUseException("User has 5 active orders");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserInUseException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(409);
            assertThat(response.getBody().getError()).isEqualTo("User In Use");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.USER_IN_USE);
        }
    }

    @Nested
    @DisplayName("Category Exception Tests")
    class CategoryExceptionTests {

        @Test
        @DisplayName("Should handle CategoryNotFoundException with NOT_FOUND")
        void testHandleCategoryNotFoundException() {
            // Given
            CategoryNotFoundException exception = new CategoryNotFoundException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleCategoryNotFoundException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Category Not Found");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.CATEGORY_NOT_FOUND);
        }

        @Test
        @DisplayName("Should handle InvalidCategoryException with BAD_REQUEST")
        void testHandleInvalidCategoryException() {
            // Given
            InvalidCategoryException exception = new InvalidCategoryException("Invalid category");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidCategoryException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Invalid Category");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INVALID_CATEGORY);
        }

        @Test
        @DisplayName("Should handle CategoryInUseException with CONFLICT")
        void testHandleCategoryInUseException() {
            // Given
            CategoryInUseException exception = new CategoryInUseException("Dogs", 5);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleCategoryInUseException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Category In Use");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.CATEGORY_IN_USE);
        }

        @Test
        @DisplayName("Should handle CategoryAlreadyExistsException with CONFLICT")
        void testHandleCategoryAlreadyExistsException() {
            // Given
            CategoryAlreadyExistsException exception = new CategoryAlreadyExistsException("Dogs");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleCategoryAlreadyExistsException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Category Already Exists");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.CATEGORY_ALREADY_EXISTS);
        }
    }

    @Nested
    @DisplayName("Discount Exception Tests")
    class DiscountExceptionTests {

        @Test
        @DisplayName("Should handle DiscountNotFoundException with NOT_FOUND")
        void testHandleDiscountNotFoundException() {
            // Given
            DiscountNotFoundException exception = new DiscountNotFoundException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleDiscountNotFoundException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Discount Not Found");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.DISCOUNT_NOT_FOUND);
        }

        @Test
        @DisplayName("Should handle DiscountInUseException with CONFLICT")
        void testHandleDiscountInUseException() {
            // Given
            DiscountInUseException exception = new DiscountInUseException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleDiscountInUseException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Discount In Use");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.DISCOUNT_IN_USE);
        }

        @Test
        @DisplayName("Should handle DiscountAlreadyExistsException with CONFLICT")
        void testHandleDiscountAlreadyExistsException() {
            // Given
            DiscountAlreadyExistsException exception = new DiscountAlreadyExistsException("SAVE10");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleDiscountAlreadyExistsException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Discount Already Exists");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.DISCOUNT_ALREADY_EXISTS);
        }
    }

    @Nested
    @DisplayName("Authentication Exception Tests")
    class AuthenticationExceptionTests {

        @Test
        @DisplayName("Should handle AuthenticationFailedException with BAD_REQUEST")
        void testHandleAuthenticationFailedException() {
            // Given
            AuthenticationFailedException exception = new AuthenticationFailedException("Invalid credentials");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationFailedException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Authentication Failed");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.AUTHENTICATION_FAILED);
        }

        @Test
        @DisplayName("Should handle AccessDeniedException with FORBIDDEN")
        void testHandleAccessDeniedException() {
            // Given
            AccessDeniedException exception = new AccessDeniedException("Access denied");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(403);
            assertThat(response.getBody().getError()).isEqualTo("Access Denied");
            assertThat(response.getBody().getMessage()).isEqualTo("You don't have permission to perform this operation");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.ACCESS_DENIED);
        }
    }

    @Nested
    @DisplayName("Validation Exception Tests")
    class ValidationExceptionTests {

        @Test
        @DisplayName("Should handle MethodArgumentNotValidException with BAD_REQUEST")
        void testHandleMethodArgumentNotValidException() {
            // Given
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("user", "email", "must not be blank");
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
            
            MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Validation Failed");
            assertThat(response.getBody().getMessage()).contains("email");
            assertThat(response.getBody().getMessage()).contains("must not be blank");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.VALIDATION_FAILED);
        }

        @Test
        @DisplayName("Should handle HttpMessageNotReadableException with BAD_REQUEST")
        void testHandleHttpMessageNotReadableException() {
            // Given
            HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
            when(exception.getMessage()).thenReturn("JSON parse error");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpMessageNotReadable(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Invalid Request Body");
            assertThat(response.getBody().getMessage()).isEqualTo("Request body is missing or malformed");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INVALID_REQUEST_BODY);
        }

        @Test
        @DisplayName("Should handle BindException with BAD_REQUEST")
        void testHandleBindException() {
            // Given
            BindException exception = mock(BindException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("pet", "name", "must not be null");
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleBindException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Binding Failed");
            assertThat(response.getBody().getMessage()).contains("name");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.BINDING_FAILED);
        }

        @Test
        @DisplayName("Should handle MethodArgumentTypeMismatchException with BAD_REQUEST")
        void testHandleMethodArgumentTypeMismatchException() {
            // Given
            MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
            when(exception.getName()).thenReturn("id");
            when(exception.getValue()).thenReturn("abc");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleTypeMismatchException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Invalid Parameter");
            assertThat(response.getBody().getMessage()).contains("abc");
            assertThat(response.getBody().getMessage()).contains("id");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INVALID_PARAMETER);
        }
    }

    @Nested
    @DisplayName("Generic Exception Tests")
    class GenericExceptionTests {

        @Test
        @DisplayName("Should handle IllegalArgumentException with BAD_REQUEST")
        void testHandleIllegalArgumentException() {
            // Given
            IllegalArgumentException exception = new IllegalArgumentException("Invalid argument value");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getError()).isEqualTo("Invalid Argument");
            assertThat(response.getBody().getMessage()).isEqualTo("Invalid argument value");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INVALID_ARGUMENT);
        }

        @Test
        @DisplayName("Should handle RuntimeException with INTERNAL_SERVER_ERROR")
        void testHandleRuntimeException() {
            // Given
            RuntimeException exception = new RuntimeException("Unexpected runtime error");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleRuntimeException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(500);
            assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
            assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred. Please try again later.");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INTERNAL_SERVER_ERROR);
        }

        @Test
        @DisplayName("Should handle generic Exception with INTERNAL_SERVER_ERROR")
        void testHandleGenericException() {
            // Given
            Exception exception = new Exception("Unexpected error");

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(500);
            assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
            assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred. Please try again later.");
            assertThat(response.getBody().getCode()).isEqualTo(ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }

    @Nested
    @DisplayName("Error Response Structure Tests")
    class ErrorResponseStructureTests {

        @Test
        @DisplayName("Should include correct path in error response")
        void testErrorResponsePath() {
            // Given
            when(request.getRequestURI()).thenReturn("/api/pets/123");
            PetNotFoundException exception = new PetNotFoundException(123L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handlePetNotFoundException(exception, request);

            // Then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getPath()).isEqualTo("/api/pets/123");
        }

        @Test
        @DisplayName("Should include timestamp in error response")
        void testErrorResponseTimestamp() {
            // Given
            UserNotFoundException exception = new UserNotFoundException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundException(exception, request);

            // Then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Should preserve exception message in error response")
        void testErrorResponseMessage() {
            // Given
            CategoryNotFoundException exception = new CategoryNotFoundException(1L);

            // When
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleCategoryNotFoundException(exception, request);

            // Then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getMessage()).contains("1");
        }
    }
}

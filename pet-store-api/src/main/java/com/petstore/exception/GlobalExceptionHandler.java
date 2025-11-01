package com.petstore.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for converting exceptions to error responses
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        /**
         * Handles invalid or expired discount code errors (400 Bad Request)
         *
         * @param ex      the invalid discount exception
         * @param request the current HTTP request
         * @return error response with BAD_REQUEST status
         */
        @ExceptionHandler(InvalidDiscountException.class)
        public ResponseEntity<ErrorResponse> handleInvalidDiscountException(
                        InvalidDiscountException ex, HttpServletRequest request) {
                logger.warn("Invalid discount code: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Invalid Discount Code",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.INVALID_DISCOUNT_CODE);
                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * Handles invalid payment errors (400 Bad Request)
         *
         * @param ex      the invalid payment exception
         * @param request the current HTTP request
         * @return error response with BAD_REQUEST status
         */
        @ExceptionHandler(InvalidPaymentException.class)
        public ResponseEntity<ErrorResponse> handleInvalidPaymentException(
                        InvalidPaymentException ex, HttpServletRequest request) {
                logger.warn("Invalid payment: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Invalid Payment",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.INVALID_PAYMENT);
                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * Handles unsupported payment errors (400 Bad Request)
         *
         * @param ex      the unsupported payment exception
         * @param request the current HTTP request
         * @return error response with BAD_REQUEST status
         */
        @ExceptionHandler(UnsupportedPaymentException.class)
        public ResponseEntity<ErrorResponse> handleUnsupportedPaymentException(
                        UnsupportedPaymentException ex, HttpServletRequest request) {
                logger.warn("Unsupported payment: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Unsupported Payment",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.UNSUPPORTED_PAYMENT);
                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * Handles unsupported payment type errors (400 Bad Request)
         *
         * @param ex      the unsupported payment type exception
         * @param request the current HTTP request
         * @return error response with BAD_REQUEST status
         */
        @ExceptionHandler(UnsupportedPaymentTypeException.class)
        public ResponseEntity<ErrorResponse> handleUnsupportedPaymentTypeException(
                        UnsupportedPaymentTypeException ex, HttpServletRequest request) {
                logger.warn("Unsupported payment type: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Unsupported Payment Type",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.UNSUPPORTED_PAYMENT_TYPE);
                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * Handles address not found errors (404 Not Found)
         *
         * @param ex      the address not found exception
         * @param request the current HTTP request
         * @return error response with NOT_FOUND status
         */
        @ExceptionHandler(AddressNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleAddressNotFoundException(
                        AddressNotFoundException ex, HttpServletRequest request) {
                logger.warn("Address not found: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Address Not Found",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.ADDRESS_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        /**
         * Handles address in use errors (409 Conflict)
         *
         * @param ex      the address in use exception
         * @param request the current HTTP request
         * @return error response with CONFLICT status
         */
        @ExceptionHandler(AddressInUseException.class)
        public ResponseEntity<ErrorResponse> handleAddressInUseException(
                        AddressInUseException ex, HttpServletRequest request) {
                logger.warn("Address in use: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "Address In Use",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.ADDRESS_IN_USE);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        /**
         * Handles cart item not found errors (404 Not Found)
         *
         * @param ex      the cart item not found exception
         * @param request the current HTTP request
         * @return error response with NOT_FOUND status
         */
        @ExceptionHandler(CartItemNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleCartItemNotFoundException(
                        CartItemNotFoundException ex, HttpServletRequest request) {
                logger.warn("Cart item not found: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Cart Item Not Found",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.CART_ITEM_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        /**
         * Handles cart empty errors (404 Not Found)
         *
         * @param ex      the cart empty exception
         * @param request the current HTTP request
         * @return error response with BAD_REQUEST status
         */
        @ExceptionHandler(CartEmptyException.class)
        public ResponseEntity<ErrorResponse> handleCartEmptyException(
                        CartEmptyException ex, HttpServletRequest request) {
                logger.warn("Cart is empty: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Cart Is Empty",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.CART_IS_EMPTY);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        /**
         * Handles order not found errors (404 Not Found)
         *
         * @param ex      the order not found exception
         * @param request the current HTTP request
         * @return error response with NOT_FOUND status
         */
        @ExceptionHandler(OrderNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleOrderNotFoundException(
                        OrderNotFoundException ex, HttpServletRequest request) {
                logger.warn("Order not found: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Order Not Found",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.ORDER_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        /**
         * Handles address not found errors (404 Not Found)
         *
         * @param ex      the address not found exception
         * @param request the current HTTP request
         * @return error response with NOT_FOUND status
         */
        @ExceptionHandler(PetNotFoundException.class)
        public ResponseEntity<ErrorResponse> handlePetNotFoundException(
                        PetNotFoundException ex, HttpServletRequest request) {
                logger.warn("Pet not found: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Pet Not Found",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.PET_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        /**
         * Handles pet already sold errors (404 Not Found)
         *
         * @param ex      the pet already sold exception
         * @param request the current HTTP request
         * @return error response with CONFLICT status
         */
        @ExceptionHandler(PetAlreadySoldException.class)
        public ResponseEntity<ErrorResponse> handlePetAlreadySoldException(
                        PetAlreadySoldException ex, HttpServletRequest request) {
                logger.warn("Pet already sold: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "Pet Already Sold",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.PET_ALREADY_SOLD);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        /**
         * Handles pet is already in cart errors (404 Not Found)
         *
         * @param ex      the pet is already in cart exception
         * @param request the current HTTP request
         * @return error response with CONFLICT status
         */
        @ExceptionHandler(PetAlreadyExistInUserCartException.class)
        public ResponseEntity<ErrorResponse> handlePetAlreadyExistInUserCartException(
                        PetAlreadyExistInUserCartException ex, HttpServletRequest request) {
                logger.warn("Pet already exists in user cart: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "Pet Already Exists in User Cart",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.PET_ALREADY_EXISTS_IN_USER_CART);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        /**
         * Handles user cart not found errors (404 Not Found)
         *
         * @param ex      the user cart not found exception
         * @param request the current HTTP request
         * @return error response with NOT_FOUND status
         */
        @ExceptionHandler(UserCartNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserCartNotFoundException(
                        UserCartNotFoundException ex, HttpServletRequest request) {
                logger.warn("User cart not found: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "User Cart Not Found",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.USER_CART_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        /**
         * Handles user not found errors (404 Not Found)
         *
         * @param ex      the user not found exception
         * @param request the current HTTP request
         * @return error response with NOT_FOUND status
         */
        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserNotFoundException(
                        UserNotFoundException ex, HttpServletRequest request) {
                logger.warn("User not found: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "User Not Found",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.USER_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        /**
         * Handles authentication failures (400 Bad Request)
         *
         * @param ex      the authentication exception
         * @param request the current HTTP request
         * @return error response with BAD_REQUEST status
         */
        @ExceptionHandler(AuthenticationFailedException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationFailedException(
                        AuthenticationFailedException ex, HttpServletRequest request) {
                logger.warn("Authentication failed: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Authentication Failed",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.AUTHENTICATION_FAILED);
                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * Handles category not found errors (404 Not Found)
         *
         * @param ex      the category not found exception
         * @param request the current HTTP request
         * @return error response with NOT_FOUND status
         */
        @ExceptionHandler(CategoryNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(
                        CategoryNotFoundException ex, HttpServletRequest request) {
                logger.warn("Category not found: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Category Not Found",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.CATEGORY_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        /**
         * Handles invalid category errors (400 Bad Request)
         *
         * @param ex      the invalid category exception
         * @param request the current HTTP request
         * @return error response with BAD_REQUEST status
         */
        @ExceptionHandler(InvalidCategoryException.class)
        public ResponseEntity<ErrorResponse> handleInvalidCategoryException(
                        InvalidCategoryException ex, HttpServletRequest request) {
                logger.warn("Invalid category: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Invalid Category",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.INVALID_CATEGORY);
                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * Handles category deletion when it's still in use (409 Conflict)
         *
         * @param ex      the category in use exception
         * @param request the current HTTP request
         * @return error response with CONFLICT status
         */
        @ExceptionHandler(CategoryInUseException.class)
        public ResponseEntity<ErrorResponse> handleCategoryInUseException(
                        CategoryInUseException ex, HttpServletRequest request) {

                logger.warn("Category deletion blocked: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "Category In Use",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.CATEGORY_IN_USE);

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        /**
         * Handles category already exists errors (409 Conflict)
         *
         * @param ex      the category already exists exception
         * @param request the current HTTP request
         * @return error response with CONFLICT status
         */
        @ExceptionHandler(CategoryAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleCategoryAlreadyExistsException(
                        CategoryAlreadyExistsException ex, HttpServletRequest request) {
                logger.warn("Category already exists: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "Category Already Exists",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.CATEGORY_ALREADY_EXISTS);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        /**
         * Handles discount not found errors (404 Not Found)
         *
         * @param ex      the discount not found exception
         * @param request the current HTTP request
         * @return error response with NOT_FOUND status
         */
        @ExceptionHandler(DiscountNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleDiscountNotFoundException(
                        DiscountNotFoundException ex, HttpServletRequest request) {
                logger.warn("Discount not found: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Discount Not Found",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.DISCOUNT_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

         /**
         * Handles discount in use errors (409 Conflict)
         *
         * @param ex      the discount in use exception
         * @param request the current HTTP request
         * @return error response with CONFLICT status
         */
        @ExceptionHandler(DiscountInUseException.class)
        public ResponseEntity<ErrorResponse> handleDiscountInUseException(
                        DiscountInUseException ex, HttpServletRequest request) {
                logger.warn("Discount in use: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "Discount In Use",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.DISCOUNT_IN_USE);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        /**
         * Handles category already exists errors (409 Conflict)
         *
         * @param ex      the category already exists exception
         * @param request the current HTTP request
         * @return error response with CONFLICT status
         */
        @ExceptionHandler(DiscountAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleDiscountAlreadyExistsException(
                        DiscountAlreadyExistsException ex, HttpServletRequest request) {
                logger.warn("Discount already exists: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "Discount Already Exists",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.DISCOUNT_ALREADY_EXISTS);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        /**
         * Handles order ownership errors (403 Forbidden)
         *
         * @param ex      the order ownership exception
         * @param request the current HTTP request
         * @return error response with FORBIDDEN status
         */
        @ExceptionHandler(OrderOwnershipException.class)
        public ResponseEntity<ErrorResponse> handleOrderOwnershipException(
                        OrderOwnershipException ex, HttpServletRequest request) {
                logger.warn("Order access denied: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.FORBIDDEN.value(),
                                "Order Access Denied",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.ORDER_ACCESS_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        /**
         * Handles invalid pet errors (400 Bad Request)
         *
         * @param ex      the invalid pet exception
         * @param request the current HTTP request
         * @return error response with BAD_REQUEST status
         */
        @ExceptionHandler(InvalidPetException.class)
        public ResponseEntity<ErrorResponse> handleInvalidPetException(
                        InvalidPetException ex, HttpServletRequest request) {
                logger.warn("Invalid pet: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Invalid Pet",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.INVALID_PET);
                return ResponseEntity.badRequest().body(errorResponse);
        }

        /*
         * Handles invalid user errors (400 Bad Request)
         */
        @ExceptionHandler(InvalidUserException.class)
        public ResponseEntity<ErrorResponse> handleInvalidUserException(
                        InvalidUserException ex, HttpServletRequest request) {
                logger.warn("Invalid user: {}", ex.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Invalid User",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.INVALID_USER);
                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * Handles user deletion when they have pets (409 Conflict)
         * 
         * @param ex      the user in use exception
         * @param request the current HTTP request
         * @return error response with CONFLICT status
         */
        @ExceptionHandler(UserInUseException.class)
        public ResponseEntity<ErrorResponse> handleUserInUseException(
                        UserInUseException ex, HttpServletRequest request) {

                logger.warn("User deletion blocked: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "User In Use",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.USER_IN_USE);

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        /**
         * Handles validation errors from @Valid annotations (400 Bad Request)
         *
         * @param ex      the validation exception
         * @param request the current HTTP request
         * @return error response with BAD_REQUEST status and validation details
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach((error) -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                logger.warn("Validation failed: {}", errors);

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Validation Failed",
                                "Invalid input data: " + errors.toString(),
                                request.getRequestURI(),
                                ErrorCodes.VALIDATION_FAILED);

                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * Handles malformed JSON in request bodies (400 Bad Request)
         *
         * @param ex      the HTTP message not readable exception
         * @param request the current HTTP request
         * @return error response with BAD_REQUEST status
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
                        HttpMessageNotReadableException ex, HttpServletRequest request) {

                logger.warn("Invalid JSON: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Invalid Request Body",
                                "Request body is missing or malformed",
                                request.getRequestURI(),
                                ErrorCodes.INVALID_REQUEST_BODY);

                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * Handles binding validation errors (400 Bad Request)
         *
         * @param ex      the binding exception
         * @param request the current HTTP request
         * @return error response with BAD_REQUEST status and binding errors
         */
        @ExceptionHandler(BindException.class)
        public ResponseEntity<ErrorResponse> handleBindException(
                        BindException ex, HttpServletRequest request) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach((error) -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                logger.warn("Binding failed: {}", errors);

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Binding Failed",
                                "Invalid input data: " + errors.toString(),
                                request.getRequestURI(),
                                ErrorCodes.BINDING_FAILED);

                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * Handles type mismatch in request parameters (400 Bad Request)
         *
         * @param ex      the type mismatch exception
         * @param request the current HTTP request
         * @return error response with BAD_REQUEST status
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleTypeMismatchException(
                        MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

                logger.warn("Type mismatch: {} for parameter {}", ex.getValue(), ex.getName());

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Invalid Parameter",
                                String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName()),
                                request.getRequestURI(),
                                ErrorCodes.INVALID_PARAMETER);

                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * Handles authorization failures (403 Forbidden)
         *
         * @param ex      the access denied exception
         * @param request the current HTTP request
         * @return error response with FORBIDDEN status
         */
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                        AccessDeniedException ex, HttpServletRequest request) {

                logger.warn("Access denied: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.FORBIDDEN.value(),
                                "Access Denied",
                                "You don't have permission to perform this operation",
                                request.getRequestURI(),
                                ErrorCodes.ACCESS_DENIED);

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        /**
         * Handles invalid argument values (400 Bad Request)
         *
         * @param ex      the illegal argument exception
         * @param request the current HTTP request
         * @return error response with BAD_REQUEST status
         */
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex, HttpServletRequest request) {

                logger.warn("Illegal argument: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Invalid Argument",
                                ex.getMessage(),
                                request.getRequestURI(),
                                ErrorCodes.INVALID_ARGUMENT);

                return ResponseEntity.badRequest().body(errorResponse);
        }

        /**
         * Handles unexpected runtime errors (500 Internal Server Error)
         *
         * @param ex      the runtime exception
         * @param request the current HTTP request
         * @return error response with INTERNAL_SERVER_ERROR status
         */
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ErrorResponse> handleRuntimeException(
                        RuntimeException ex, HttpServletRequest request) {

                logger.error("Runtime exception occurred: ", ex);

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error",
                                "An unexpected error occurred. Please try again later.",
                                request.getRequestURI(),
                                ErrorCodes.INTERNAL_SERVER_ERROR);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        /**
         * Handles all other unexpected errors (500 Internal Server Error)
         *
         * @param ex      the unexpected exception
         * @param request the current HTTP request
         * @return error response with INTERNAL_SERVER_ERROR status
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex, HttpServletRequest request) {

                logger.error("Unexpected exception occurred: ", ex);

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error",
                                "An unexpected error occurred. Please try again later.",
                                request.getRequestURI(),
                                ErrorCodes.INTERNAL_SERVER_ERROR);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
}
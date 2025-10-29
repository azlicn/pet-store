package com.petstore.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
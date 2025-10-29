package com.petstore.exception;

/**
 * Centralized error code constants for API error responses.
 */
public final class ErrorCodes {
    private ErrorCodes() {}


    public static final String AUTHENTICATION_FAILED = "ERROR_403";
    public static final String VALIDATION_FAILED = "ERROR_400";
    public static final String BINDING_FAILED = "ERROR_400";
    public static final String INVALID_PARAMETER = "ERROR_400";
    public static final String ACCESS_DENIED = "ERROR_403";
    public static final String INVALID_ARGUMENT = "ERROR_400";
    public static final String INTERNAL_SERVER_ERROR = "ERROR_500";


    public static final String USER_NOT_FOUND = "ERROR_1001";
    public static final String USER_IN_USE = "ERROR_1002";

    public static final String ADDRESS_NOT_FOUND = "ERROR_2001";
    public static final String ADDRESS_IN_USE = "ERROR_2002";

    public static final String CATEGORY_IN_USE = "ERROR_3001";
    public static final String CATEGORY_ALREADY_EXISTS = "ERROR_3002";

    public static final String PET_NOT_FOUND = "ERROR_4001";
    public static final String PET_ALREADY_SOLD = "ERROR_4002";
    public static final String PET_ALREADY_EXISTS_IN_USER_CART = "ERROR_4003";


    public static final String INVALID_DISCOUNT_CODE = "ERROR_5001";
    public static final String DISCOUNT_ALREADY_EXISTS = "ERROR_5002";

    public static final String CART_ITEM_NOT_FOUND = "ERROR_6001";
    public static final String USER_CART_NOT_FOUND = "ERROR_6002";

    public static final String ORDER_ACCESS_DENIED = "ERROR_7001";
    public static final String ORDER_NOT_FOUND = "ERROR_7002";
    

}

package com.petstore.util;

public class OrderNumberGenerator {

    /**
     * Generates a unique order number
     *
     * @return unique order number as a String
     */
    public static String generateOrderNumber() {

        String millis = String.valueOf(System.currentTimeMillis());
        String last6 = millis.length() > 6 ? millis.substring(millis.length() - 6)
                : String.format("%06d", Integer.parseInt(millis));
        int randomSuffix = (int) (Math.random() * 10000); // 0 to 9999
        String randomStr = String.format("%04d", randomSuffix);
        return "ORD-" + last6 + randomStr;
        
    }

}

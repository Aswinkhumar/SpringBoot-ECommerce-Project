package com.eCommerce.application.Exceptions;

public class APIException extends RuntimeException{

    String message;

    public APIException() {}
    public APIException(String message) {
        super(message);
    }
}

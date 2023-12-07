package com.coppel.webclient;

public class UnexpectedResponseException extends RuntimeException{

    public UnexpectedResponseException(String message) {
        super(message);
    }
}

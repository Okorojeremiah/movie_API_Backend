package org.africa.movieflix.exceptions;

public class EmptyFileException extends RuntimeException{

    public EmptyFileException(String message){
        super(message);
    }
}

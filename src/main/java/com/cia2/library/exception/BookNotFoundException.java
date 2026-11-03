package com.cia2.library.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Book " + id + " not found");
    }
}

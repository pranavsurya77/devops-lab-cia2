package com.cia2.library.exception;

/** Thrown when an action is not allowed in the book's current state, e.g. borrowing a book that is already out. */
public class BookStateException extends RuntimeException {

    public BookStateException(String message) {
        super(message);
    }
}

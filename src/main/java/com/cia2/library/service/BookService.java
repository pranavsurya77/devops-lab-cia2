package com.cia2.library.service;

import com.cia2.library.dto.BookRequest;
import com.cia2.library.exception.BookNotFoundException;
import com.cia2.library.exception.BookStateException;
import com.cia2.library.model.Book;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** In-memory book store. Data resets on restart, which keeps the cloud deployment free of a database. */
@Service
public class BookService {

    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public BookService() {
        add(new BookRequest("Clean Code", "Robert C. Martin", "9780132350884"));
        add(new BookRequest("The Pragmatic Programmer", "Andrew Hunt, David Thomas", "9780135957059"));
        add(new BookRequest("Continuous Delivery", "Jez Humble, David Farley", "9780321601919"));
    }

    public List<Book> findAll(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        return books.values().stream()
                .filter(b -> q.isEmpty()
                        || b.getTitle().toLowerCase(Locale.ROOT).contains(q)
                        || b.getAuthor().toLowerCase(Locale.ROOT).contains(q))
                .sorted(Comparator.comparing(Book::getId))
                .toList();
    }

    public Book findById(Long id) {
        Book book = books.get(id);
        if (book == null) {
            throw new BookNotFoundException(id);
        }
        return book;
    }

    public Book add(BookRequest request) {
        Book book = new Book(nextId.getAndIncrement(), request.title().trim(), request.author().trim(), request.isbn());
        books.put(book.getId(), book);
        return book;
    }

    public synchronized Book borrow(Long id, String borrower) {
        Book book = findById(id);
        if (!book.isAvailable()) {
            throw new BookStateException("Book " + id + " is already borrowed by " + book.getBorrowedBy());
        }
        book.setBorrowedBy(borrower.trim());
        return book;
    }

    public synchronized Book returnBook(Long id) {
        Book book = findById(id);
        if (book.isAvailable()) {
            throw new BookStateException("Book " + id + " is not currently borrowed");
        }
        book.setBorrowedBy(null);
        return book;
    }

    public void delete(Long id) {
        Book book = findById(id);
        if (!book.isAvailable()) {
            throw new BookStateException("Book " + id + " cannot be deleted while it is borrowed");
        }
        books.remove(id);
    }
}

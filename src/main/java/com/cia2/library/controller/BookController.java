package com.cia2.library.controller;

import com.cia2.library.dto.BookRequest;
import com.cia2.library.dto.BorrowRequest;
import com.cia2.library.model.Book;
import com.cia2.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/boks")
@Tag(name = "Books", description = "Manage the library catalogue and loans")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "List books", description = "Optional q filters by title or author (case-insensitive)")
    @GetMapping
    public List<Book> list(@RequestParam(name = "q", required = false) String query) {
        return bookService.findAll(query);
    }

    @Operation(summary = "Get a book by id", description = "404 if the book does not exist")
    @GetMapping("/{id}")
    public Book get(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @Operation(summary = "Add a book", description = "title and author are required, isbn is optional")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book add(@Valid @RequestBody BookRequest request) {
        return bookService.add(request);
    }

    @Operation(summary = "Borrow a book", description = "409 if the book is already borrowed")
    @PostMapping("/{id}/borrow")
    public Book borrow(@PathVariable Long id, @Valid @RequestBody BorrowRequest request) {
        return bookService.borrow(id, request.borrower());
    }

    @Operation(summary = "Return a borrowed book", description = "409 if the book is not currently borrowed")
    @PostMapping("/{id}/return")
    public Book returnBook(@PathVariable Long id) {
        return bookService.returnBook(id);
    }

    @Operation(summary = "Delete a book", description = "409 if the book is currently borrowed")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
}

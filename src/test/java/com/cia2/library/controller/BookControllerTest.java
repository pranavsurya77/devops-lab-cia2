package com.cia2.library.controller;

import com.cia2.library.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(BookService.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void listsAllBooks() throws Exception {
        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void searchesBooks() throws Exception {
        mvc.perform(get("/api/books").param("q", "pragmatic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("The Pragmatic Programmer"));
    }

    @Test
    void addsBook() throws Exception {
        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Refactoring\",\"author\":\"Martin Fowler\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void rejectsBookWithoutTitle() throws Exception {
        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\":\"Nobody\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("title is required"));
    }

    @Test
    void borrowsThenRejectsSecondBorrow() throws Exception {
        String body = "{\"borrower\":\"Alice\"}";

        mvc.perform(post("/api/books/1/borrow").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowedBy").value("Alice"))
                .andExpect(jsonPath("$.available").value(false));

        mvc.perform(post("/api/books/1/borrow").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void returnsBorrowedBook() throws Exception {
        mvc.perform(post("/api/books/2/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"borrower\":\"Bob\"}"));

        mvc.perform(post("/api/books/2/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void deletesBook() throws Exception {
        mvc.perform(delete("/api/books/3")).andExpect(status().isNoContent());
        mvc.perform(get("/api/books/3")).andExpect(status().isNotFound());
    }

    @Test
    void unknownBookReturns404() throws Exception {
        mvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book 99 not found"));
    }
}

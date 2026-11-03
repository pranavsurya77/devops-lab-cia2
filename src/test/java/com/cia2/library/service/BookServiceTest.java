package com.cia2.library.service;

import com.cia2.library.dto.BookRequest;
import com.cia2.library.exception.BookNotFoundException;
import com.cia2.library.exception.BookStateException;
import com.cia2.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookServiceTest {

    private BookService service;

    @BeforeEach
    void setUp() {
        service = new BookService();
    }

    @Test
    void startsWithSeedBooks() {
        assertThat(service.findAll(null)).hasSize(3);
    }

    @Test
    void addAssignsIdAndIsAvailable() {
        Book book = service.add(new BookRequest("Refactoring", "Martin Fowler", "9780134757599"));

        assertThat(book.getId()).isEqualTo(4L);
        assertThat(book.isAvailable()).isTrue();
        assertThat(service.findById(4L).getTitle()).isEqualTo("Refactoring");
    }

    @Test
    void searchMatchesTitleOrAuthorIgnoringCase() {
        assertThat(service.findAll("CLEAN")).extracting(Book::getTitle).containsExactly("Clean Code");
        assertThat(service.findAll("humble")).extracting(Book::getTitle).containsExactly("Continuous Delivery");
        assertThat(service.findAll("no-such-book")).isEmpty();
    }

    @Test
    void borrowMarksBookAsUnavailable() {
        Book book = service.borrow(1L, "Alice");

        assertThat(book.isAvailable()).isFalse();
        assertThat(book.getBorrowedBy()).isEqualTo("Alice");
    }

    @Test
    void cannotBorrowBookThatIsAlreadyBorrowed() {
        service.borrow(1L, "Alice");

        assertThatThrownBy(() -> service.borrow(1L, "Bob"))
                .isInstanceOf(BookStateException.class)
                .hasMessageContaining("already borrowed by Alice");
    }

    @Test
    void returnMakesBookAvailableAgain() {
        service.borrow(2L, "Alice");

        Book book = service.returnBook(2L);

        assertThat(book.isAvailable()).isTrue();
        assertThat(book.getBorrowedBy()).isNull();
    }

    @Test
    void cannotReturnBookThatIsNotBorrowed() {
        assertThatThrownBy(() -> service.returnBook(2L)).isInstanceOf(BookStateException.class);
    }

    @Test
    void deleteRemovesAvailableBook() {
        service.delete(3L);

        assertThatThrownBy(() -> service.findById(3L)).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void cannotDeleteBorrowedBook() {
        service.borrow(3L, "Alice");

        assertThatThrownBy(() -> service.delete(3L)).isInstanceOf(BookStateException.class);
    }

    @Test
    void unknownIdThrowsNotFound() {
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book 99 not found");
    }
}

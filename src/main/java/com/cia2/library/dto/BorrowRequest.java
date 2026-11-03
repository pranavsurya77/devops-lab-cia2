package com.cia2.library.dto;

import jakarta.validation.constraints.NotBlank;

public record BorrowRequest(@NotBlank(message = "borrower is required") String borrower) {
}

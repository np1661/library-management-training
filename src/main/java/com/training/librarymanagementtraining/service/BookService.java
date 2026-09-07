package com.training.librarymanagementtraining.service;

import com.training.librarymanagementtraining.dto.request.CreateRequest;
import com.training.librarymanagementtraining.dto.request.UpdateRequest;
import com.training.librarymanagementtraining.dto.response.ApiResponse;
import com.training.librarymanagementtraining.dto.response.BookResponse;
import com.training.librarymanagementtraining.entity.Book;
import com.training.librarymanagementtraining.exception.BookNotFoundException;
import com.training.librarymanagementtraining.repository.BookRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public ApiResponse createBook(CreateRequest request) {

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .category(request.getCategory())
                .available(true)
                .build();

        bookRepository.save(book);

        return ApiResponse.builder()
                .message("Book created successfully")
                .build();
    }

    public List<BookResponse> getAllBooks() {

        List<Book> books = bookRepository.findAll();

        return books.stream()
                .map(book -> BookResponse.builder()
                        .bookId(book.getId())
                        .bookName(book.getTitle())
                        .bookAuthor(book.getAuthor())
                        .category(book.getCategory())
                        .available(book.getAvailable())
                        .build())
                .toList();
    }

    public BookResponse getBookById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(()->new BookNotFoundException("Book not found with id: " + id));

        return BookResponse.builder()
                .bookId(book.getId())
                .bookName(book.getTitle())
                .bookAuthor(book.getAuthor())
                .category(book.getCategory())
                .available(book.getAvailable())
                .build();
    }

    public BookResponse updateBook(Long id, @Valid UpdateRequest request) {

        Book book = bookRepository.findById(id)
                .orElseThrow(()->new BookNotFoundException("Book not found with id: " + id));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setCategory(request.getCategory());
        book.setAvailable(request.getAvailable());

        Book updatedBook = bookRepository.save(book);

        return BookResponse.builder()
                .bookId(updatedBook.getId())
                .bookName(updatedBook.getTitle())
                .bookAuthor(updatedBook.getAuthor())
                .category(updatedBook.getCategory())
                .available(updatedBook.getAvailable())
                .build();
    }

    public ApiResponse deleteBook(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(()->new BookNotFoundException("Book not found with id: " + id));

        bookRepository.delete(book);

        return ApiResponse.builder()
                .message("Book deleted successfully")
                .build();
    }

    public List<BookResponse> getBooksByName(String title) {

       List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);

        return books.stream()
                .map(book -> BookResponse.builder()
                        .bookId(book.getId())
                        .bookName(book.getTitle())
                        .bookAuthor(book.getAuthor())
                        .category(book.getCategory())
                        .available(book.getAvailable())
                        .build())
                .toList();
    }
}

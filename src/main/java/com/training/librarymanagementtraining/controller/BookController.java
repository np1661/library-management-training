package com.training.librarymanagementtraining.controller;

import com.training.librarymanagementtraining.dto.request.CreateRequest;
import com.training.librarymanagementtraining.dto.request.UpdateRequest;
import com.training.librarymanagementtraining.dto.response.ApiResponse;
import com.training.librarymanagementtraining.dto.response.BookResponse;
import com.training.librarymanagementtraining.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<ApiResponse> createBook(@RequestBody CreateRequest request){
        ApiResponse response = bookService.createBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks(){
        List<BookResponse> responses = bookService.getAllBooks();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id){
        BookResponse response = bookService.getBookById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateRequest request){
        BookResponse response = bookService.updateBook(id,request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable Long id){
        ApiResponse response = bookService.deleteBook(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> getBooksByName(@RequestParam String title){
        List<BookResponse> responses = bookService.getBooksByName(title);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }




}

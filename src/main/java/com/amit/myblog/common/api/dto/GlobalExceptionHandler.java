package com.amit.myblog.common.api.dto;

import com.amit.myblog.comment.service.exception.CommentNotFoundException;
import com.amit.myblog.comment.service.exception.InvalidCommentException;
import com.amit.myblog.post.service.exception.ImageUpsertException;
import com.amit.myblog.post.service.exception.InvalidImageException;
import com.amit.myblog.post.service.exception.InvalidPostException;
import com.amit.myblog.post.service.exception.PostNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public final class GlobalExceptionHandler {

    @ExceptionHandler(value = ImageUpsertException.class)
    public ResponseEntity<ErrorDto> handleImageUpsertException(ImageUpsertException exception, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(exception.getMessage(), request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDto);
    }

    @ExceptionHandler(value = InvalidImageException.class)
    public ResponseEntity<ErrorDto> handleInvalidImageException(InvalidImageException exception, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(exception.getMessage(), request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDto);
    }

    @ExceptionHandler(value = InvalidPostException.class)
    public ResponseEntity<ErrorDto> handleInvalidPostException(InvalidPostException exception, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(exception.getMessage(), request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDto);
    }

    @ExceptionHandler(value = PostNotFoundException.class)
    public ResponseEntity<ErrorDto> handlePostNotFoundException(PostNotFoundException exception, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(exception.getMessage(), request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDto);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException exception, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(exception.getMessage(), request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDto);
    }

    @ExceptionHandler(value = CommentNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCommentNotFoundException(CommentNotFoundException exception, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(exception.getMessage(), request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDto);
    }

    @ExceptionHandler(value = InvalidCommentException.class)
    public ResponseEntity<ErrorDto> handleInvalidCommentException(InvalidCommentException exception, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(exception.getMessage(), request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDto);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception exception, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(exception.getMessage(), request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDto);
    }

}

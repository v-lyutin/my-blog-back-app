package com.amit.myblog.common.api;

import com.amit.myblog.common.api.dto.ErrorDto;
import com.amit.myblog.common.excpetion.ResourceNotFoundException;
import com.amit.myblog.common.excpetion.ServiceException;
import com.amit.myblog.post.service.exception.ImageUpsertException;
import com.amit.myblog.post.service.exception.InvalidImageException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public final class GlobalExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(exception.getMessage(), request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDto);
    }

    @ExceptionHandler(value = ServiceException.class)
    public ResponseEntity<ErrorDto> handleServiceException(ServiceException exception, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(exception.getMessage(), request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDto);
    }

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

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception exception, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(exception.getMessage(), request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDto);
    }

}

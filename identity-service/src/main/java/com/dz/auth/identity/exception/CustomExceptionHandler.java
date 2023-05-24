package com.dz.auth.identity.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler implements AccessDeniedHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> userAlreadyExistsException(UserAlreadyExistsException exception) {
        log.error(exception.getClass().getName() + " [" + exception.getMessage() + "]");
        exception.printStackTrace();
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), exception.getMessage());
        return new ResponseEntity<>(pd, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error(exception.getClass().getName() + " [" + exception.getMessage() + "]");
        exception.printStackTrace();
        List<String> errorMessages = new ArrayList<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors())
            errorMessages.add(error.getDefaultMessage());
        for (ObjectError error : exception.getBindingResult().getGlobalErrors())
            errorMessages.add(error.getDefaultMessage());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), errorMessages.toString());
        return new ResponseEntity<>(pd, HttpStatus.BAD_REQUEST);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.error(accessDeniedException.getClass().getName() + " [" + accessDeniedException.getMessage() + "]");
        accessDeniedException.printStackTrace();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), "Access Denied!");
        response.getOutputStream().println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(pd));
    }

}

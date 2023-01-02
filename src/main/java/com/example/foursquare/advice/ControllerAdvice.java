package com.example.foursquare.advice;

import com.example.foursquare.exception.InvalidUserCredentialException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(InvalidUserCredentialException.class)
    public ResponseEntity<?> exception(InvalidUserCredentialException invalidUserCredentialException) {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("error message","Invalid User Credentials");
        return new ResponseEntity<>(stringStringMap, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElement(NoSuchElementException noSuchElementException) {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("error message","no element found");
        return new ResponseEntity<>(stringStringMap,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> noData(EmptyResultDataAccessException e) {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("error message","Data not found");
        return new ResponseEntity<>(stringStringMap, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        Map<String, String> stringStringMap = new HashMap<>();
        methodArgumentNotValidException.getBindingResult().getFieldErrors().forEach(errors -> {
            stringStringMap.put(errors.getField(), errors.getDefaultMessage());
        });
        return stringStringMap;
    }

    @ExceptionHandler(SQLGrammarException.class)
    public Map<String, String> handleSQLGrammarException(SQLGrammarException exception) {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("error message", exception.getMessage());
        return stringStringMap;
    }


    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException sqlIntegrityConstraintViolationException) {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("error message",sqlIntegrityConstraintViolationException.getMessage());
        return  new ResponseEntity<>(stringStringMap,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> ConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("error message", "Already Created");
        return  new ResponseEntity<>(stringStringMap,HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> DataIntegrityViolationException(DataIntegrityViolationException exception) {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("error message", " already added");
        return  new ResponseEntity<>(stringStringMap,HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> UserNotFound(UsernameNotFoundException usernameNotFoundException) {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("error message", "User not found");
        return  new ResponseEntity<>(stringStringMap,HttpStatus.BAD_REQUEST);

    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> invalidRequestMethod() {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("error message", "Please check the request method");
        return  new ResponseEntity<>(stringStringMap,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredential() {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("error message", "Invalid user name or password");
        return  new ResponseEntity<>(stringStringMap,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> disabled() {
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("error message", "user disabled");
        return  new ResponseEntity<>(stringStringMap,HttpStatus.BAD_REQUEST);
    }

}

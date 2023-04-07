package com.logispin.wallet.api.error;

import com.logispin.wallet.exceptions.InsufficientFundsException;
import com.logispin.wallet.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path.Node;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestErrorControllerAdvice {

  private static final String ERROR_SOURCE = "CORE_SERVICE";

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Errors> handleIllegalArgumentException(IllegalArgumentException ex) {
    var error = new ErrorBlock(ERROR_SOURCE, ErrorReason.BAD_REQUEST, ex.getMessage(), false);
    return new ResponseEntity<>(new Errors(error), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Errors> handleBadCredentialsException(BadCredentialsException ex) {
    var error = new ErrorBlock(ERROR_SOURCE, ErrorReason.UNAUTHORIZED, ex.getMessage(), false);
    return new ResponseEntity<>(new Errors(error), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Errors> handleNoSuchElementException(NoSuchElementException ex) {
    var error = new ErrorBlock(ERROR_SOURCE, ErrorReason.NOT_FOUND, ex.getMessage(), false);
    return new ResponseEntity<>(new Errors(error), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InternalError.class)
  public ResponseEntity<Errors> handleInternalError(InternalError ex) {
    var error =
        new ErrorBlock(ERROR_SOURCE, ErrorReason.INTERNAL_SERVER_ERROR, ex.getMessage(), false);
    return new ResponseEntity<>(new Errors(error), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(UnsupportedOperationException.class)
  public ResponseEntity<Errors> handleUnsupportedOperationEError(UnsupportedOperationException ex) {
    var error = new ErrorBlock(ERROR_SOURCE, ErrorReason.NOT_IMPLEMENTED, ex.getMessage(), false);
    return new ResponseEntity<>(new Errors(error), HttpStatus.NOT_IMPLEMENTED);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Errors> exceptionHandler(ValidationException ex) {
    var error = new ErrorBlock(ERROR_SOURCE, ErrorReason.BAD_REQUEST, ex.getMessage(), false);
    return new ResponseEntity<>(new Errors(error), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InsufficientFundsException.class)
  public ResponseEntity<Errors> exceptionHandler(InsufficientFundsException ex) {
    var error = new ErrorBlock(ERROR_SOURCE, ErrorReason.BAD_REQUEST, ex.getMessage(), false);
    return new ResponseEntity<>(new Errors(error), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Errors> exceptionHandler(ResourceNotFoundException ex) {
    var error = new ErrorBlock(ERROR_SOURCE, ErrorReason.NOT_FOUND, ex.getMessage(), false);
    return new ResponseEntity<>(new Errors(error), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Errors> exceptionHandler(ConstraintViolationException ex) {
    final Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

    List<String> errors =
        constraintViolations.stream()
            .map(
                err -> {
                  String field = null;
                  for (Node node : err.getPropertyPath()) {
                    field = node.getName();
                  }
                  return field + ": " + err.getMessage();
                })
            .collect(Collectors.toList());

    var error = new ErrorBlock(ERROR_SOURCE, ErrorReason.BAD_REQUEST, errors);
    return new ResponseEntity<>(new Errors(error), HttpStatus.BAD_REQUEST);
  }

  // error handle for @Valid
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Errors> exceptionHandler(MethodArgumentNotValidException ex) {
    // Get all field errors
    List<String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.toList());

    var error = new ErrorBlock(ERROR_SOURCE, ErrorReason.BAD_REQUEST, errors);
    return new ResponseEntity<>(new Errors(error), HttpStatus.BAD_REQUEST);
  }
}

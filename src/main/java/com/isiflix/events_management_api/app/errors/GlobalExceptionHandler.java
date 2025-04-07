package com.isiflix.events_management_api.app.errors;

import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import net.logstash.logback.argument.StructuredArguments;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final StatusCodeMapper statusCodeMapper;

    public GlobalExceptionHandler() {
        this.statusCodeMapper = new StatusCodeMapper();
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<StandardErrorResponse> handleBusinessRuleViolationException(BusinessRuleViolationException e) {
        final var violationCode = e.getViolationCode();
        final var metadata = Optional.ofNullable(e.getMetadata()).orElse(Collections.emptyMap());

        logger.error("Business Rule Violation Exception Occurred",
                StructuredArguments.keyValue("code", violationCode.toString()),
                StructuredArguments.keyValue("message", e.getMessage()),
                StructuredArguments.keyValue("metadata", metadata),
                StructuredArguments.keyValue("stackTrace", e.getStackTrace())
        );

        final int statusCode = this.statusCodeMapper.of(violationCode);
        return ResponseEntity
                .status(statusCode)
                .body(StandardErrorResponse.of(e));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public StandardErrorResponse handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        final Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.putIfAbsent(fieldName, new ArrayList<>());
            List<String> fieldErrors = errors.get(fieldName);
            fieldErrors.add(errorMessage);
        });

        return new StandardErrorResponse("invalid-payload", "Invalid Payload", errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public StandardErrorResponse handleValidationExceptions(
            HandlerMethodValidationException ex) {
        final Map<String, List<String>> errors = new HashMap<>();

        ex.getParameterValidationResults().forEach((error) -> {
            String parameterName = error.getMethodParameter().getParameterName();
            List<String> errorMessage = error.getResolvableErrors()
                    .stream()
                    .map(MessageSourceResolvable::getDefaultMessage)
                    .toList();
            errors.put(parameterName, errorMessage);
        });

        return new StandardErrorResponse("invalid-parameters", "Invalid Parameters", errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public StandardErrorResponse handleValidationExceptions(
            MethodArgumentTypeMismatchException ex) {
        final Map<String, List<String>> errors = new HashMap<>();

        final var parameterName = ex.getPropertyName();
        errors.put(parameterName, List.of(ex.getMessage()));

        return new StandardErrorResponse("invalid-parameters", "Invalid Parameters", errors);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public StandardErrorResponse handleNotReadable() {
        return new StandardErrorResponse("not-readable-payload", "Payload not readable", null);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public StandardErrorResponse handleNotFound(NoResourceFoundException ex) {
        return new StandardErrorResponse(
                "not-found",
                "The request resource %s was not found".formatted(ex.getResourcePath()),
                null
        );
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public StandardErrorResponse handleNotAllowedMethod(HttpRequestMethodNotSupportedException ex) {
        return new StandardErrorResponse(
                "method-not-allowed",
                "The request method %s is not allowed".formatted(ex.getMethod()),
                null
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public StandardErrorResponse handleUnexpectedException(Exception e) {
        logger.error("An unexpected error occurred", StructuredArguments.keyValue("stackTrace", e.getStackTrace()));
        return new StandardErrorResponse("unexpected-error", "An unexpected error occurred", null);
    }
}

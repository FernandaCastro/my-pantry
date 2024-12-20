package com.fcastro.pantryservice.exception;

import com.fcastro.commons.exception.ApplicationError;
import com.fcastro.commons.exception.ResourceNotFoundException;
import com.fcastro.security.core.exception.TokenVerifierException;
import com.fcastro.security.exception.AccessControlNotDefinedException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final Map<Class<? extends Throwable>, String> exceptionTypes = new HashMap<>();

    static {
        exceptionTypes.put(MissingServletRequestPartException.class, "missing-request-part");
        exceptionTypes.put(MissingServletRequestParameterException.class, "missing-request-parameter");
        exceptionTypes.put(MethodArgumentTypeMismatchException.class, "method-argument-type-mismatch");
        exceptionTypes.put(MethodArgumentNotValidException.class, "argument-not-valid");

        exceptionTypes.put(HttpMessageNotReadableException.class, "http-message-not-readable");
        exceptionTypes.put(HttpMediaTypeNotAcceptableException.class, "http-media-type-not-acceptable");
        exceptionTypes.put(HttpMediaTypeNotSupportedException.class, "http-media-type-not-supported");
        exceptionTypes.put(HttpRequestMethodNotSupportedException.class, "method-not-allowed");

        exceptionTypes.put(DataAccessException.class, "database-error");
        exceptionTypes.put(ExpiredJwtException.class, "token-expired");
        exceptionTypes.put(AccessDeniedException.class, "access-denied");

        exceptionTypes.put(DatabaseConstraintException.class, "application-error");
        exceptionTypes.put(ResourceNotFoundException.class, "application-error");
        exceptionTypes.put(QuantityNotAvailableException.class, "application-error");
        exceptionTypes.put(PantryNotActiveException.class, "application-error");
        exceptionTypes.put(RequestParamExpectedException.class, "application-error");
        exceptionTypes.put(AccessControlNotDefinedException.class, "application-error");
        exceptionTypes.put(PantryAndProductAccountGroupInconsistentException.class, "application-error");
    }

    @ExceptionHandler(value = {QuantityNotAvailableException.class,
            PantryNotActiveException.class,
            RequestParamExpectedException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            HttpMessageNotReadableException.class,
            DatabaseConstraintException.class,
            AccessControlNotDefinedException.class})
    public ResponseEntity<?> badRequest(final Exception ex, final HttpServletRequest request) {

        final var error = ApplicationError.builder()
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorType(exceptionTypes.get(ex.getClass()))
                .errorMessage(ex.getLocalizedMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<?> notAcceptable(final HttpMediaTypeNotAcceptableException ex, final HttpServletRequest request) {
        final var error = ApplicationError.builder()
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .errorType(exceptionTypes.get(ex.getClass()))
                .errorMessage(ex.getLocalizedMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> methodNotAllowed(final HttpRequestMethodNotSupportedException ex, final HttpServletRequest request) {
        final var error = ApplicationError.builder()
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .errorType(exceptionTypes.get(ex.getClass()))
                .errorMessage(ex.getLocalizedMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> unsupportedMediaType(final HttpMediaTypeNotSupportedException ex, final HttpServletRequest request) {
        final var error = ApplicationError.builder()
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .errorType(exceptionTypes.get(ex.getClass()))
                .errorMessage(ex.getLocalizedMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> badRequest(final MethodArgumentNotValidException ex, final HttpServletRequest request) {

        final var errorsSet = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage).collect(Collectors.toSet());
        final var errorMessages = String.join(",", errorsSet);

        final var error = ApplicationError.builder()
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorType(exceptionTypes.get(ex.getClass()))
                .errorMessage(errorMessages)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<?> notFound(final ResourceNotFoundException ex, final HttpServletRequest request) {

        final var error = ApplicationError.builder()
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.NOT_FOUND.value())
                .errorType(exceptionTypes.get(ex.getClass()))
                .errorMessage(ex.getLocalizedMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(value = {DataAccessException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> databaseException(final DataAccessException ex, final HttpServletRequest request) {

        LOGGER.error("Database error ", ex);

        var error = ApplicationError.builder()
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorType(exceptionTypes.get(ex.getClass()))
                .errorMessage(ex.getLocalizedMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(value = {TokenVerifierException.class, AccessDeniedException.class})
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> expiredToken(final Exception ex, final WebRequest request) {

        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI().toString();

        final var error = ApplicationError.builder()
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.FORBIDDEN.value())
                .errorType(exceptionTypes.get(ex.getClass()))
                .errorMessage(ex.getLocalizedMessage())
                .path(requestUri)
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> generalException(final Exception ex, final HttpServletRequest request) {

        var error = ApplicationError.builder()
                .timestamp(Clock.systemUTC().millis())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorType(ex.getClass().getSimpleName())
                .errorMessage("Please contact the support. " + ex.getLocalizedMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

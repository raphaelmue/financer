package org.financer.server.application.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.financer.server.application.api.error.RestErrorMessage;
import org.financer.server.application.api.error.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    public RestExceptionHandler(MessageSource messageSource, ObjectMapper objectMapper, HttpServletRequest request) {
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestErrorMessage> handleArgumentNotValidException(MethodArgumentNotValidException exception, Locale locale) {
        BindingResult result = exception.getBindingResult();
        List<String> errorMessages = result.getAllErrors()
                .stream()
                .map(objectError -> messageSource.getMessage(objectError, locale))
                .collect(Collectors.toList());
        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new RestErrorMessage(HttpStatus.BAD_REQUEST, request.getRequestURI(), errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestErrorMessage> handleAccessDeniedException(AccessDeniedException exception, Locale locale) {
        String errorMessage = messageSource.getMessage("exception.unauthorizedToken", null, locale);
        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new RestErrorMessage(HttpStatus.UNAUTHORIZED, request.getRequestURI(), errorMessage),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RestException.class)
    public ResponseEntity<RestErrorMessage> handleRestExceptions(RestException exception, Locale locale) {
        String errorMessage = messageSource.getMessage(exception.getMessageKey(), exception.getArguments(), locale);
        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new RestErrorMessage(exception, request.getRequestURI(), errorMessage), exception.getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestErrorMessage> handleRestExceptions(RuntimeException exception, Locale locale) {
        String errorMessage = messageSource.getMessage("exception.unexpected", new String[]{}, locale);
        logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new RestErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(), errorMessage),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

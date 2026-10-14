package tn.iteam.exceptions;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tn.iteam.dtos.ErrorResponseDto;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // compte introuvable -> 404 NOT_FOUND
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleAccountNotFoundException(AccountNotFoundException exception,
                                                                           WebRequest webRequest) {
        return buildError(HttpStatus.NOT_FOUND, exception.getMessage(), webRequest);
    }

    // ms-customer répond 404 : le client lié au compte n'existe pas -> 404 NOT_FOUND
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorResponseDto> handleCustomerNotFound(FeignException.NotFound exception,
                                                                   WebRequest webRequest) {
        return buildError(HttpStatus.NOT_FOUND, "Customer linked to this account was not found in ms-customer",
                webRequest);
    }

    // ms-customer arrêté, non enregistré dans Eureka ou en erreur -> 503 SERVICE_UNAVAILABLE
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponseDto> handleFeignException(FeignException exception, WebRequest webRequest) {
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, "ms-customer is unavailable: " + exception.getMessage(),
                webRequest);
    }

    // toute autre erreur non prévue -> 500 INTERNAL_SERVER_ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception, WebRequest webRequest) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), webRequest);
    }

    private ResponseEntity<ErrorResponseDto> buildError(HttpStatus status, String message, WebRequest webRequest) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                webRequest.getDescription(false), status, message, LocalDateTime.now());
        return new ResponseEntity<>(errorResponseDto, status);
    }
}

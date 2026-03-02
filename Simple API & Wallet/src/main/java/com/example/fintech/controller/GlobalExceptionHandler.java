package com.example.fintech.controller;

import com.example.fintech.dto.ErrorResponse;
import com.example.fintech.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Esta classe atua como um interceptor global para exceções lançadas por qualquer Controller.
 * Utiliza a anotação @RestControllerAdvice, que combina @ControllerAdvice e @ResponseBody,
 * garantindo que as respostas de erro sejam serializadas automaticamente em formato JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura exceções do tipo BusinessException, que são lançadas quando uma regra de
     * negócio é violada (ex: saldo insuficiente ou transferência para a própria conta).
     * * @param ex A instância da exceção capturada contendo a mensagem de erro.
     * @return Um ResponseEntity contendo o objeto ErrorResponse e o status HTTP 400 (Bad Request).
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Captura exceções de validação de argumentos (MethodArgumentNotValidException).
     * Estas exceções são disparadas automaticamente pelo Spring quando as anotações
     * de validação (como @NotNull, @Email ou @Positive) falham nos DTOs de entrada.
     * * @param ex Exceção de validação disparada pelo Bean Validation.
     * @return Resposta estruturada com o status 400.
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                "Erro de validação nos dados enviados",
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Tratamento genérico para qualquer outra exceção não prevista (RuntimeExceptions, etc).
     * Atua como uma "rede de segurança" para evitar que o servidor exponha o Stack Trace
     * completo, o que seria um risco de segurança (Information Exposure).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                "Ocorreu um erro interno inesperado do lado do servidor.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

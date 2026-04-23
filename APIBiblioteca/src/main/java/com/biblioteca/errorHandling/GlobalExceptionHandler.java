package com.biblioteca.errorHandling;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.biblioteca.errorHandling.Exception.ResourceNotFoundException;

@RestControllerAdvice // Combinación de @ControllerAdvice y @ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetalles> manejarResourceNotFoundException(
            ResourceNotFoundException exception, 
            WebRequest webRequest) {
        
        ErrorDetalles error = new ErrorDetalles(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false), // extracción de la ruta donde se lanzó la excepción
                "NOT_FOUND"
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetalles> manejarExcepcionesGlobales(
            Exception exception, 
            WebRequest webRequest) {
        
        ErrorDetalles error = new ErrorDetalles(
                LocalDateTime.now(),
                "Ha ocurrido un error interno en el servidor",
                webRequest.getDescription(false),
                "INTERNAL_SERVER_ERROR"
        );
        
        // si me da tiempo más adelante lo voy a actualizar y mandaré el mensaje a un archivo de log (o implementaré algún servicio que corresponda)
        System.err.println("Error crítico: " + exception.getMessage());
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
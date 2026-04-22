package com.biblioteca.errorHandling;

import java.time.LocalDateTime;

public class ErrorDetalles {
    
    private LocalDateTime timestamp;
    private String mensaje;
    private String detalles;
    private String codigoHttp;

    public ErrorDetalles(LocalDateTime timestamp, String mensaje, String detalles, String codigoHttp) {
        this.timestamp = timestamp;
        this.mensaje = mensaje;
        this.detalles = detalles;
        this.codigoHttp = codigoHttp;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getMensaje() { return mensaje; }
    public String getDetalles() { return detalles; }
    public String getCodigoHttp() { return codigoHttp; }
}
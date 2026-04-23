package com.biblioteca.errorHandling.Exception;

public class ResourceNotFoundException extends RuntimeException  {

 public ResourceNotFoundException(String mensaje) {
	 
     super(mensaje);
     
 }
}
package com.biblioteca.errorHandling.Exeption;

public class ResourceNotFoundException extends RuntimeException  {

 public ResourceNotFoundException(String mensaje) {
	 
     super(mensaje);
     
 }
}
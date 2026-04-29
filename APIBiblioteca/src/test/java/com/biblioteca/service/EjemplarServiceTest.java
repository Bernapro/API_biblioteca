package com.biblioteca.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.biblioteca.dto.EjemplarRegistroDTO;
import com.biblioteca.entity.Ejemplar;
import com.biblioteca.entity.Libro;
import com.biblioteca.enums.CondicionEjemplar;
import com.biblioteca.enums.EstadoEjemplar;
import com.biblioteca.errorHandling.Exception.*;
import com.biblioteca.repository.EjemplarRepository;
import com.biblioteca.repository.LibroRepository;

// Le decimos a JUnit que vamos a usar la magia de Mockito
@ExtendWith(MockitoExtension.class) 
public class EjemplarServiceTest {

    // 1. Creamos los objetos FALSOS (Los Mocks)
    @Mock
    private EjemplarRepository ejemplarRepository;

    @Mock
    private LibroRepository libroRepository;

    // 2. Inyectamos los Mocks dentro del Servicio REAL que queremos probar
    @InjectMocks
    private EjemplarService ejemplarService;

    // ==========================================
    // PRUEBA 1: El Camino Feliz (Éxito)
    // ==========================================
    @Test
    void registrarEjemplar_DebeAsignarEstadoDisponible_CuandoEsExitoso() {
        // A. PREPARACIÓN (Arrange)
        EjemplarRegistroDTO dto = new EjemplarRegistroDTO("123-ISBN", "ADQ-001", CondicionEjemplar.NUEVO);
        Libro libroFalso = new Libro();
        libroFalso.setIsbn("123-ISBN");

        // Entrenamos a los Mocks (La mentira piadosa)
        when(ejemplarRepository.existsByNoAdquisicion("ADQ-001")).thenReturn(false);
        when(libroRepository.findById("123-ISBN")).thenReturn(Optional.of(libroFalso));
        
        // Cuando el servicio intente guardar, el mock simplemente devolverá lo mismo que se le pasó
        when(ejemplarRepository.save(any(Ejemplar.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // B. EJECUCIÓN (Act)
        Ejemplar resultado = ejemplarService.registrarEjemplar(dto);

        // C. VERIFICACIÓN (Assert) - ¡Aquí llegamos al fondo del asunto!
        assertNotNull(resultado);
        assertEquals("ADQ-001", resultado.getNoAdquisicion());
        // Verificamos tu regla de negocio estricta: ¡Debe nacer DISPONIBLE obligatoriamente!
        assertEquals(EstadoEjemplar.DISPONIBLE, resultado.getEstado()); 
        
        // Verificamos que el servicio efectivamente llamó al repositorio una vez
        verify(ejemplarRepository, times(1)).save(any(Ejemplar.class));
    }

    // ==========================================
    // PRUEBA 2: La Defensa Arquitectónica (Fallo)
    // ==========================================
    @Test
    void registrarEjemplar_DebeLanzarExcepcion_CuandoLibroNoExiste() {
        // A. PREPARACIÓN
        EjemplarRegistroDTO dto = new EjemplarRegistroDTO("ISBN-FANTASMA", "ADQ-002", CondicionEjemplar.BUENO);
        
        when(ejemplarRepository.existsByNoAdquisicion("ADQ-002")).thenReturn(false);
        // Entrenamos al mock para simular que no encontró el libro
        when(libroRepository.findById("ISBN-FANTASMA")).thenReturn(Optional.empty());

        // B & C. EJECUCIÓN Y VERIFICACIÓN (Esperamos que tu arquitectura detone la excepción)
        ResourceNotFoundException excepcion = assertThrows(ResourceNotFoundException.class, () -> {
            ejemplarService.registrarEjemplar(dto);
        });

        // Verificamos que el mensaje sea el correcto
        assertTrue(excepcion.getMessage().contains("El libro con ISBN ISBN-FANTASMA no existe"));
        
        // Verificamos que el servicio JAMÁS intentó guardar nada en la BD
        verify(ejemplarRepository, never()).save(any(Ejemplar.class));
    }
    
    
}
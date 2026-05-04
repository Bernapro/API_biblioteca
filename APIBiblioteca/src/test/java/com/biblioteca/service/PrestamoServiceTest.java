package com.biblioteca.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.biblioteca.dto.PrestamoRegistroDTO;
import com.biblioteca.entity.DetallePrestamo;
import com.biblioteca.entity.Ejemplar;
import com.biblioteca.entity.Prestamo;
import com.biblioteca.enums.EstadoEjemplar;
import com.biblioteca.errorHandling.Exception.ResourceNotFoundException;
import com.biblioteca.repository.EjemplarRepository;
import com.biblioteca.repository.PrestamoRepository;

@ExtendWith(MockitoExtension.class)
public class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private EjemplarRepository ejemplarRepository;

    @InjectMocks
    private PrestamoService prestamoService;

    // ==========================================
    // PRUEBA 1: El Camino Feliz (Consistencia y Mutación)
    // ==========================================
    @Test
    void registrarPrestamo_DebeCrearPrestamoYCambiarEstados_CuandoEjemplaresEstanDisponibles() {
        // A. PREPARACIÓN (Arrange)
        UUID idEjemplar1 = UUID.randomUUID();
        UUID idEjemplar2 = UUID.randomUUID();
        
        // El DTO de entrada con la matrícula/ID del usuario y los 2 libros
        PrestamoRegistroDTO dto = new PrestamoRegistroDTO("MATRICULA-2026", Set.of(idEjemplar1, idEjemplar2), LocalDate.now().plusDays(4));

        // Simulamos los ejemplares físicos en estado correcto
        Ejemplar ejemplar1 = new Ejemplar();
        ejemplar1.setId(idEjemplar1);
        ejemplar1.setNoAdquisicion("ADQ-01");
        ejemplar1.setEstado(EstadoEjemplar.DISPONIBLE);

        Ejemplar ejemplar2 = new Ejemplar();
        ejemplar2.setId(idEjemplar2);
        ejemplar2.setNoAdquisicion("ADQ-02");
        ejemplar2.setEstado(EstadoEjemplar.DISPONIBLE);

        // Entrenamos a los Mocks
        when(ejemplarRepository.findById(idEjemplar1)).thenReturn(Optional.of(ejemplar1));
        when(ejemplarRepository.findById(idEjemplar2)).thenReturn(Optional.of(ejemplar2));
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(i -> i.getArgument(0));

        // B. EJECUCIÓN (Act)
        Prestamo resultado = prestamoService.registrarPrestamo(dto);

        // C. VERIFICACIÓN (Assert)
        assertNotNull(resultado);
        assertEquals("MATRICULA-2026", resultado.getUsuario());
        assertEquals(LocalDate.now(), resultado.getFechaInicio());
        // Verificamos tu regla de los 4 días
        assertEquals(LocalDate.now().plusDays(4), resultado.getFechaLimite()); 
        assertEquals(2, resultado.getCantidadLibrosPrestados()); // Probamos tu método @Transient

        // VERIFICACIÓN CRÍTICA: Confirmamos que la RAM mutó el estado de los objetos
        assertEquals(EstadoEjemplar.PRESTADO, ejemplar1.getEstado());
        assertEquals(EstadoEjemplar.PRESTADO, ejemplar2.getEstado());

        verify(prestamoRepository, times(1)).save(any(Prestamo.class));
    }

    // ==========================================
    // PRUEBA 2: El Escudo ACID (Rollback Atómico)
    // ==========================================
    @Test
    void registrarPrestamo_DebeAbortarTransaccion_CuandoUnEjemplarNoEstaDisponible() {
        // A. PREPARACIÓN
        UUID idEjemplarBueno = UUID.randomUUID();
        UUID idEjemplarMalo = UUID.randomUUID(); // Este libro ya lo tiene alguien más
        
        PrestamoRegistroDTO dto = new PrestamoRegistroDTO("MATRICULA-2026", Set.of(idEjemplarBueno, idEjemplarMalo), LocalDate.now().plusDays(4));

        Ejemplar ejemplarMalo = new Ejemplar();
        ejemplarMalo.setId(idEjemplarMalo);
        ejemplarMalo.setNoAdquisicion("ADQ-MALO");
        // ¡Este es el detonante!
        ejemplarMalo.setEstado(EstadoEjemplar.PRESTADO); 

        // Solo necesitamos entrenar al mock para el libro malo, el orden del Set puede variar
        when(ejemplarRepository.findById(idEjemplarMalo)).thenReturn(Optional.of(ejemplarMalo));
        // Para evitar NullPointerExceptions si el test evalúa el bueno primero
        lenient().when(ejemplarRepository.findById(idEjemplarBueno)).thenReturn(Optional.of(new Ejemplar()));

        // B & C. EJECUCIÓN Y VERIFICACIÓN
        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> {
            prestamoService.registrarPrestamo(dto);
        });

        assertTrue(excepcion.getMessage().contains("no se puede prestar"));
        
        // LA PRUEBA DEFINITIVA: Aseguramos que la transacción se cortó de tajo y NUNCA se llamó al save
        verify(prestamoRepository, never()).save(any(Prestamo.class));
    }
    
 // ==========================================
    // PRUEBA 3: Devolución Exitosa (Mutación en RAM)
    // ==========================================
    @Test
    void finalizarPrestamo_DebeAsignarFechaYLiberarEjemplares_CuandoEsExitoso() {
        // A. PREPARACIÓN
        UUID idPrestamo = UUID.randomUUID();
        
        Prestamo prestamoActivo = new Prestamo();
        prestamoActivo.setId(idPrestamo);
        prestamoActivo.setUsuario("EST-2026-001");
        prestamoActivo.setFechaDevolucion(null); // Aún no devuelto

        // Preparamos un ejemplar que actualmente está PRESTADO
        Ejemplar ejemplarPrestado = new Ejemplar();
        ejemplarPrestado.setNoAdquisicion("ADQ-01");
        ejemplarPrestado.setEstado(EstadoEjemplar.PRESTADO);

        DetallePrestamo detalle = new DetallePrestamo();
        detalle.setPrestamo(prestamoActivo);
        detalle.setEjemplar(ejemplarPrestado);

        prestamoActivo.setDetalles(Set.of(detalle));

        // Entrenamos al mock
        when(prestamoRepository.findById(idPrestamo)).thenReturn(Optional.of(prestamoActivo));

        // B. EJECUCIÓN
        Prestamo resultado = prestamoService.finalizarPrestamo(idPrestamo);

        // C. VERIFICACIÓN (Aserciones de memoria)
        assertNotNull(resultado);
        
        // 1. Verificamos que se estampó la fecha de hoy
        assertEquals(LocalDate.now(), resultado.getFechaDevolucion());
        
        // 2. Verificamos la mutación profunda: El ejemplar físico DEBE estar DISPONIBLE
        // Como las colecciones son referencias en memoria, revisar 'ejemplarPrestado' es suficiente
        assertEquals(EstadoEjemplar.DISPONIBLE, ejemplarPrestado.getEstado());
        
        // Nos aseguramos de que el repositorio fue consultado exactamente 1 vez
        verify(prestamoRepository, times(1)).findById(idPrestamo);
    }

    // ==========================================
    // PRUEBA 4: Defensa contra Préstamos Fantasma
    // ==========================================
    @Test
    void finalizarPrestamo_DebeLanzarExcepcion_CuandoPrestamoNoExiste() {
        // A. PREPARACIÓN
        UUID idInvalido = UUID.randomUUID();
        when(prestamoRepository.findById(idInvalido)).thenReturn(Optional.empty());

        // B & C. EJECUCIÓN Y VERIFICACIÓN
        ResourceNotFoundException excepcion = assertThrows(ResourceNotFoundException.class, () -> {
            prestamoService.finalizarPrestamo(idInvalido);
        });

        assertTrue(excepcion.getMessage().contains("no existe"));
    }

    // ==========================================
    // PRUEBA 5: Defensa contra Devoluciones Dobles
    // ==========================================
    @Test
    void finalizarPrestamo_DebeLanzarExcepcion_CuandoPrestamoYaEstaFinalizado() {
        // A. PREPARACIÓN
        UUID idPrestamo = UUID.randomUUID();
        
        Prestamo prestamoCerrado = new Prestamo();
        prestamoCerrado.setId(idPrestamo);
        // Simulamos que este libro se devolvió ayer
        prestamoCerrado.setFechaDevolucion(LocalDate.now().minusDays(1)); 

        when(prestamoRepository.findById(idPrestamo)).thenReturn(Optional.of(prestamoCerrado));

        // B & C. EJECUCIÓN Y VERIFICACIÓN
        IllegalStateException excepcion = assertThrows(IllegalStateException.class, () -> {
            prestamoService.finalizarPrestamo(idPrestamo);
        });

        assertTrue(excepcion.getMessage().contains("ya fue finalizado"));
        
        // Verificación de integridad: La fecha original NO debió cambiar a la de hoy
        assertEquals(LocalDate.now().minusDays(1), prestamoCerrado.getFechaDevolucion());
    }
}
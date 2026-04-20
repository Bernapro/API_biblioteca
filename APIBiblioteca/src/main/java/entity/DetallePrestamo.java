package entity;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "detalle_prestamo",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_prestamo_ejemplar", 
            columnNames = {"id_prestamo", "id_ejemplar"})
    }
)

public class DetallePrestamo {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETALLE_GEN")
	@SequenceGenerator(
        name = "DETALLE_GEN",               
        sequenceName = "DETALLE_SEQ",       //nombre rea en PostgreSQL
        allocationSize = 1                // id++
    )
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_prestamo", nullable = false)
	@JsonIgnore
	private Prestamo prestamo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_ejemplar", nullable = false)
	private Ejemplar ejemplar;
	
	
	public DetallePrestamo() {
		
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Prestamo getPrestamo() {
		return prestamo;
	}


	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}


	public Ejemplar getEjemplar() {
		return ejemplar;
	}


	public void setEjemplar(Ejemplar ejemplar) {
		this.ejemplar = ejemplar;
	}


	@Override
	public int hashCode() {
	    return Objects.hash(
	        ejemplar != null ? ejemplar.getId() : null, 
	        prestamo != null ? prestamo.getId() : null
	    );
	}


	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null) return false;
	    if (getClass() != obj.getClass()) return false;
	    
	    DetallePrestamo other = (DetallePrestamo) obj;
	    
	    UUID esteEjemplarId = (this.ejemplar != null) ? this.ejemplar.getId() : null;
	    UUID otroEjemplarId = (other.ejemplar != null) ? other.ejemplar.getId() : null;
	    
	    UUID estePrestamoId = (this.prestamo != null) ? this.prestamo.getId() : null;
	    UUID otroPrestamoId = (other.prestamo != null) ? other.prestamo.getId() : null;
	    
	    return Objects.equals(esteEjemplarId, otroEjemplarId) && 
	           Objects.equals(estePrestamoId, otroPrestamoId);
	}
}

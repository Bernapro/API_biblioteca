package com.biblioteca.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.dto.LibroCompletoDTO;
import com.biblioteca.dto.LibroRegistroDTO;
import com.biblioteca.dto.LibroResumenDTO;
import com.biblioteca.dto.PaginaRespuestaDTO;
import com.biblioteca.entity.Autor;
import com.biblioteca.entity.Categoria;
import com.biblioteca.entity.Editorial;
import com.biblioteca.entity.Ejemplar;
import com.biblioteca.entity.Libro;
import com.biblioteca.enums.CondicionEjemplar;
import com.biblioteca.enums.EstadoEjemplar;
import com.biblioteca.errorHandling.Exception.*;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.CategoriaRepository;
import com.biblioteca.repository.EditorialRepository;
import com.biblioteca.repository.EjemplarRepository;
import com.biblioteca.repository.LibroRepository;

@Service
public class LibroService {

	private final LibroRepository libroRepository;
	private final EditorialRepository editorialRepository;
	private final AutorRepository autorRepository;
	private final CategoriaRepository categoriaRepository;
	private final EjemplarRepository ejemplarRepository;

	public LibroService(LibroRepository libroRepository, EditorialRepository editorialRepository,
			AutorRepository autorRepository, CategoriaRepository categoriaRepository, EjemplarRepository ejmEjemplarRepository) {
		this.libroRepository = libroRepository;
		this.editorialRepository = editorialRepository;
		this.autorRepository = autorRepository;
		this.categoriaRepository = categoriaRepository;
		this.ejemplarRepository = ejmEjemplarRepository;
	}

	@Transactional
	public Libro registrarLibro(LibroRegistroDTO dto) {

		// se mapean los campos propios del objeto Libro
		Libro libro = new Libro();
		libro.setIsbn(dto.isbn());
		libro.setTitulo(dto.titulo());
		libro.setEdicion(dto.edicion());
		libro.setFechaPublicacion(dto.fechaPublicacion());
		libro.setDewey(dto.dewey());
		libro.setClasificacionDelCongreso(dto.clasificacionDelCongreso());
		libro.setClasificacionDecimalUniversal(dto.clasificacionDecimalUniversal());

		// luego se validan los id´s de los objtos que se relacionan con el libro
		// evidentemente, si persisten en la BD se setea el atributo del obj libro
		if (dto.editorial() != null && !dto.editorial().isBlank()) {
			Editorial editorial = editorialRepository.encontrarEditorialNormalizada(dto.editorial()).orElseGet(() -> {
				Editorial ed = new Editorial();
				ed.setNombre(dto.editorial());
				return editorialRepository.save(ed);
			});
			libro.setEditorial(editorial);
		} else {
			throw new IllegalArgumentException("Todo libro debe tener una editorial asociada.");
		}

		// Es una lógica muy similar para la validación de autores, editoriales y
		// categorias
		// más adelante veré como abstraerlo para evitar repetir código
		if (dto.autores() != null && !dto.autores().isEmpty()) {
			Set<Autor> autores = new HashSet<>();
			/*
			 * for (Long id : dto.autoresIds()) { Autor autor =
			 * autorRepository.findById(id).orElseThrow( () -> new
			 * ResourceNotFoundException("El autor con ID " + id + " no está registrado."));
			 * autores.add(autor); } libro.setAutores(autores);
			 */
			for (String nombreIngresado : dto.autores()) {
				if (nombreIngresado.isBlank()) {
					throw new IllegalArgumentException("El autor está vacío");
				}
				Autor autorResuelto = autorRepository.encontrarAutorNormalizado(nombreIngresado).orElseGet(() -> {
					Autor nuevoAutor = new Autor();
					nuevoAutor.setPseudonimo(nombreIngresado.trim());
					return autorRepository.save(nuevoAutor);
				});
				autores.add(autorResuelto);
			}
			libro.setAutores(autores);
		} else {
			throw new IllegalArgumentException("El libro debe tener al menos un autor.");
		}

		if (dto.categorias() != null && !dto.categorias().isEmpty()) {
			Set<Categoria> categorias = new HashSet<>();

			for (String nombre : dto.categorias()) {
				if (nombre.isBlank()) {
					throw new IllegalArgumentException("La categoría está vacía");
				}
				categorias.add(categoriaRepository.encontrarCategoriaNormalizada(nombre).orElseGet(() -> {
					Categoria nueva = new Categoria();
					nueva.setNombre(nombre.trim());
					return categoriaRepository.save(nueva);
				}));
			}
			libro.setCategorias(categorias);
		}
		
		libro.setEjemplares(new HashSet<>());
		for (int i = 0; i < dto.nEjemplares(); i++) {

			Long secuencia = ejemplarRepository.obtenerSiguienteSecuenciaAdquisicion();
			String noAdquisicion = String.format("ADQ-%07d", secuencia);

			Ejemplar ejemplar = new Ejemplar();
			ejemplar.setNoAdquisicion(noAdquisicion);
			ejemplar.setEstado(EstadoEjemplar.DISPONIBLE);
			ejemplar.setCondicion(CondicionEjemplar.NUEVO);

			libro.addEjemplar(ejemplar);
		}

		// si llegue hasta aquí supongo que todo salió bien
		return libroRepository.save(libro);
	}

	@Transactional(readOnly = true)
	public PaginaRespuestaDTO<LibroResumenDTO> obtenerCatalogo(int numeroPagina, int tamanoPagina) {

		Pageable peticionPagina = PageRequest.of(numeroPagina, tamanoPagina);

		Page<LibroResumenDTO> paginaLibros = libroRepository.obtenerCatalogoResumido(peticionPagina);

		return new PaginaRespuestaDTO<>(paginaLibros.getContent(), paginaLibros.getNumber(), paginaLibros.getSize(),
				paginaLibros.getTotalElements(), paginaLibros.getTotalPages(), paginaLibros.isLast());
	}

	@Transactional(readOnly = true)
	public LibroCompletoDTO obtenerLibroCompleto(String isbn) {
		Libro libro = libroRepository.obtenerLibroConEditorial(isbn)
				.orElseThrow(() -> new ResourceNotFoundException("El libro con ISBN " + isbn + " no existe."));
		/*
		 * Utilizo Streams para que sea más fácil construir la lista, solo necesito los
		 * nombres de los autores y categorías
		 */

		String[] nombresAutores = libro.getAutores().stream().map(Autor::getPseudonimo).toArray(String[]::new);

		String[] nombresCategorias = libro.getCategorias().stream().map(Categoria::getNombre).toArray(String[]::new);

		// por si acaso
		String nombreEditorial = (libro.getEditorial() != null) ? libro.getEditorial().getNombre()
				: "Publicación propia";

		return new LibroCompletoDTO(libro.getIsbn(), libro.getTitulo(), nombreEditorial, libro.getEdicion(),
				nombresAutores, libro.getFechaPublicacion(), nombresCategorias, libro.getDewey(),
				libro.getClasificacionDelCongreso(), libro.getClasificacionDecimalUniversal());
	}
}
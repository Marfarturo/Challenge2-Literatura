package com.aluracursos.challenge2_literatura.principal;

import com.aluracursos.challenge2_literatura.model.Autor;
import com.aluracursos.challenge2_literatura.repository.AutorRepository;
import com.aluracursos.challenge2_literatura.model.Libro;
import com.aluracursos.challenge2_literatura.repository.LibroRepository;
import com.aluracursos.challenge2_literatura.model.Datos;
import com.aluracursos.challenge2_literatura.model.DatosLibros;
import com.aluracursos.challenge2_literatura.service.ConsumoApi;
import com.aluracursos.challenge2_literatura.service.ConvierteDatos;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private static final String URL = "https://gutendex.com/books/";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private Integer opcion = 6;
    private Scanner scanner = new Scanner(System.in);
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    private DatosLibros getLibro(String nombreLibro) {
        String json = consumoApi.obtenerDatos(URL + "?search=" + nombreLibro.replace(" ", "+"));
        List<DatosLibros> libros = convierteDatos.obtenerDatos(json, Datos.class).resultados();
        Optional<DatosLibros> libro = libros.stream()
                .filter(l -> l.titulo().toLowerCase().contains(nombreLibro.toLowerCase()))
                .findFirst();
        if (libro.isPresent()) {
            return libro.get();
        }
        System.out.println("El libro no encontrado!");
        return null;
    }

    private void leerLibro(Libro libro) {
        System.out.println("----- LIBRO -----");
        System.out.println("Titulo: " + libro.getTitulo());
        System.out.println("Autor: " + libro.getAutor().getNombre());
        System.out.println("Idioma: " + libro.getIdioma());
        System.out.println("Numero de descargas: " + libro.getNumeroDeDescargas());
        System.out.println("----------\n");
    }

    private void leerAutor(Autor autor) {
        System.out.println("Autor: " + autor.getNombre());
        System.out.println("Fecha de nacimiento: " + autor.getFechaDeNacimiento());
        System.out.println("Fecha de fallecimiento: " + autor.getFechaMuerte());
        List<String> libros = autor.getLibro().stream()
                .map(l -> l.getTitulo())
                .collect(Collectors.toList());
        System.out.println("Libros: " + libros + "\n");
    }

    public void mostrarMenu() {
        while (opcion != 0) {
            System.out.println("""
                    \nDigite la opcion que desea realizar:
                    1- Buscar libro por titulo.
                    2- Listar libros registrados.
                    3- Listar autores registrados.
                    4- Listar autores vivos de acuerdo al año que desee.
                    5- Listar libros por idioma.
                    0- Salir de la Aplicación.
                    """);

            opcion = scanner.nextInt();
            if (opcion == 1) {
                System.out.println("Ingrese el nombre del libro a buscar:");
                String nombreLibro = scanner.next();
                Libro libro = new Libro(getLibro(nombreLibro));
                leerLibro(libro);
                libroRepository.save(libro);
            } else if (opcion == 2) {
                List<Libro> libro = libroRepository.findAll();
                libro.stream()
                        .forEach(this::leerLibro);
            } else if (opcion == 3) {
                List<Autor> autores = autorRepository.findAll();
                autores.stream()
                        .forEach(this::leerAutor);
            } else if (opcion == 4) {
                System.out.println("Ingresa el año del autor en el cuál aún vivia y que desea buscar");
                Integer fechaMuerte = scanner.nextInt();
                List<Autor> autores = autorRepository.findByFechaMuerteGreaterThan(fechaMuerte);
                autores.stream()
                        .forEach(this::leerAutor);
            } else if (opcion == 5) {
                System.out.println("Ingrese el idioma para buscar los libros:");
                System.out.println("es - español");
                System.out.println("en - ingles");
                System.out.println("fr - frances");
                System.out.println("pt - portugues");
                String idioma = scanner.next();
                List<Libro> libros = libroRepository.findByIdioma(idioma);
                libros.stream()
                        .forEach(this::leerLibro);
            }
            else
                System.out.println("Cerrando Aplicación");

        }
    }
}





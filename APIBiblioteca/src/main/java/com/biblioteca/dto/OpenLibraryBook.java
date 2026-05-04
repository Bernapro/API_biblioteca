package com.biblioteca.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibraryBook(
    String title,
    List<OpenLibraryEntity> authors,
    List<OpenLibraryEntity> publishers,
    String publish_date,
    List<OpenLibraryEntity> subjects
) {}

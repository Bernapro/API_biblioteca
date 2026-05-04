package com.biblioteca.dto;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VolumeInfo(String title, Set<String> authors, String publisher, String publishedDate,
		Set<String> categories) {
}

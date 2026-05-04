package com.biblioteca.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleItem(VolumeInfo volumeInfo) {
}

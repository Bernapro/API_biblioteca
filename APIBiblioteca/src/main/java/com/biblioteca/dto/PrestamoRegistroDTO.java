package com.biblioteca.dto;

import java.util.Set;
import java.util.UUID;

public record PrestamoRegistroDTO(String usuario, Set<UUID> ejemplaresIds) {}
package org.example.backend.dto;

import java.util.List;

public record FilteredPageDTO<T>(List<T> content, int totalPages) { }

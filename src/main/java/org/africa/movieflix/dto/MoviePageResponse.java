package org.africa.movieflix.dto;

import java.util.List;

public record MoviePageResponse(List<MovieDTO> movieDTOs,
                                Integer pageNumber,
                                Integer pageSize,
                                long totalElements,
                                int totalPages,
                                boolean isLast) {
}

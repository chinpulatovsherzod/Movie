package com.example.movieapi.dto;

import java.util.List;

public record MoviePageResponse(List<MovieDto> moviesDto,
                                Integer pageNumber,
                                Integer pageSize,
                                long totalElements,
                                long totalPages,
                                boolean isLast ){
}

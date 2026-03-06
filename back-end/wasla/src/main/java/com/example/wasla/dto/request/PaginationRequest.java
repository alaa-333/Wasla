package com.example.wasla.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationRequest {

    private Integer page = 1;
    private Integer size = 10;
    private String sortBy = "id";
    private String direction = "DESC";


    public Pageable toPageable() {

        Sort.Direction direction1 =
                "DESC".equalsIgnoreCase(this.direction) ?  Sort.Direction.DESC: Sort.Direction.ASC;

        return PageRequest.of(page-1, size, Sort.by(direction1, sortBy));
    }
}

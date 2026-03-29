package com.example.wasla.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic pagination response wrapper.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse<T> {

    private List<T> content;
    private PageMetadata metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageMetadata {
        private Integer currentPage;
        private Integer pageSize;
        private Long totalElements;
        private Integer totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
        private boolean first;
        private boolean last;

        @JsonProperty("rangeStart")
        public long getRangeStart() {
            if (totalElements == 0) return 0;
            return (long) (currentPage - 1) * pageSize + 1;
        }

        @JsonProperty("rangeEnd")
        public long getRangeEnd() {
            if (totalElements == 0) return 0;
            long end = (long) currentPage * pageSize;
            return Math.min(end, totalElements);
        }
    }

    public static <T> PaginationResponse<T> of(Page<T> page) {
        PageMetadata metadata = PageMetadata.builder()
                .currentPage(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .first(page.isFirst())
                .last(page.isLast())
                .build();

        return new PaginationResponse<>(page.getContent(), metadata);
    }
}

package com.kidari.event.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;          // 실제 데이터 리스트
    private long totalElements;       // 전체 데이터 수
    private int totalPages;           // 전체 페이지 수
    private int pageNumber;           // 현재 페이지 번호
    private boolean hasNext;          // 다음 페이지 존재 여부

    // Spring Data Page 객체를 커스텀 DTO로 변환하는 정적 메서드
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.hasNext()
        );
    }
}

package com.kidari.event.domain.event.entity;

import com.kidari.event.common.Constant;
import com.kidari.event.domain.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Event extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime drawAt;

    private Integer totalCoinLimit;
    private Integer issuedCoinCount; // 최적화용 카운터 캐시
    private Integer userCoinLimit;

    @Enumerated(EnumType.STRING)
    private Constant.EventStatus status = Constant.EventStatus.READY;

    @Builder
    public Event(String title, LocalDateTime startAt, LocalDateTime endAt, Integer totalCoinLimit, Integer issuedCoinCount, Integer userCoinLimit) {

        this.title = title;
        this.startAt = startAt;
        this.endAt = endAt;
        this.totalCoinLimit = totalCoinLimit;
        this.issuedCoinCount = issuedCoinCount;
        this.userCoinLimit = userCoinLimit;

    }

    public void updateIssuedCoinCount(Integer issuedCoinCount) {
        this.issuedCoinCount = issuedCoinCount;
    }
}
package com.example.campus_sos.web.form;

import com.example.campus_sos.domain.sosrequest.SosRequest;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class SosRequestDto {

    private final Long id;
    private final String title;
    private final String content;
    private final String building;
    private final String openChatUrl;
    private final String requesterNickname;
    private final String status;
    private final LocalDateTime createdAt;
    private final String elapsedTime;

    public SosRequestDto(SosRequest entity) {
        this.id = entity.getId();
        this.title = entity.getTitle() != null ? entity.getTitle() : "제목 없음";
        this.content = entity.getContent() != null ? entity.getContent() : "없음";
        this.building = entity.getBuilding() != null ? entity.getBuilding().getLabel() : "미지정";
        this.openChatUrl = entity.getOpenChatUrl() != null ? entity.getOpenChatUrl() : "#";
        this.requesterNickname = entity.getRequester().getNickname();
        this.status = entity.getStatus() != null ? entity.getStatus().getLabel() : "상태 없음";

        this.createdAt = entity.getCreatedAt();
        this.elapsedTime = calculateElapsedTime(this.createdAt);
    }

    private String calculateElapsedTime(LocalDateTime createdAt) {
        if (createdAt == null) return "시간 정보 없음";

        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 60) return seconds + "초 전";
        if (seconds < 3600) return (seconds / 60) + "분 전";
        if (seconds < 86400) return (seconds / 3600) + "시간 전";
        return (seconds / 86400) + "일 전";
    }
}

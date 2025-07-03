package com.example.campus_sos.web.form;
import com.example.campus_sos.domain.sosrequest.SosRequest;
import lombok.Getter;

@Getter
public class SosRequestDto {
    private final Long id;
    private final String title;
    private final String content;
    private final String building;
    private final String openChatUrl;
    private final String requesterNickname;
    private final String status;

    public SosRequestDto(SosRequest entity) {
        this.id = entity.getId();
        this.title = entity.getTitle() != null ? entity.getTitle() : "제목 없음";
        this.content = entity.getContent() != null ? entity.getContent() : "없음";
        this.building = entity.getBuilding() != null ? entity.getBuilding().getLabel() : "미지정";
        this.openChatUrl = entity.getOpenChatUrl() != null ? entity.getOpenChatUrl() : "#";
        this.requesterNickname = entity.getRequester().getNickname();
        this.status = entity.getStatus() != null ? entity.getStatus().getLabel() : "상태 없음";
    }


}
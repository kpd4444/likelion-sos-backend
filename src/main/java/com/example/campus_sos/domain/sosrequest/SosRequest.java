package com.example.campus_sos.domain.sosrequest;

import com.example.campus_sos.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class SosRequest {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private BuildingType building;

    private String title;

    @Lob
    private String content;

    private String openChatUrl;

    @Enumerated(EnumType.STRING)
    private SosStatus status = SosStatus.IN_PROGRESS;  // 기본값 "SOS 중"

    @ManyToOne(fetch = FetchType.LAZY)
    private Member requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "helper_id")
    private Member helper;
}
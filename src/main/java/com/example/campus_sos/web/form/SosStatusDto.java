package com.example.campus_sos.web.form;

import com.example.campus_sos.domain.member.Member;
import lombok.Getter;

@Getter
public class SosStatusDto {
    private final String nickname;
    private final String level;
    private final int point;

    public SosStatusDto(Member member) {
        this.nickname = member.getNickname();
        this.level = member.getLevel().getLevel();
        this.point = member.getSosPoint();
    }
}
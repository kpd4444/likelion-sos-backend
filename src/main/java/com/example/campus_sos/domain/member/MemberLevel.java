package com.example.campus_sos.domain.member;

import lombok.Getter;

@Getter
public enum MemberLevel {
    BEGINNER("신입 학우", 0),
    ENTRY("SOS 입문자", 10),
    HELPER("도움 고수", 30),
    GUARDIAN("학우 지킴이", 60),
    LEGEND("레전드 학우", 100);

    private final String level;
    private final int minPoint;

    MemberLevel(String level, int minPoint) {
        this.level = level;
        this.minPoint = minPoint;
    }

    public static MemberLevel getLevelByPoint(int point) {
        if (point >= 100) return LEGEND;
        if (point >= 60) return GUARDIAN;
        if (point >= 30) return HELPER;
        if (point >= 10) return ENTRY;
        return BEGINNER;
    }
}
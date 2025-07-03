package com.example.campus_sos.domain.sosrequest;

import lombok.Getter;

@Getter
public enum BuildingType {
    ADMINISTRATION("1호관 (교수회관)"),
    MAIN_OFFICE("2호관 (대학본부)"),
    INFO_TECH("3호관 (정보전산원)"),
    NATURAL_SCIENCE("5호관 (자연대)"),
    ENGINEERING("8호관 (공대)"),
    SHARED_LAB("9호관 (공동실험)"),
    DESIGN("10호관 (디자인대)"),
    HUMANITIES("14호관 (경영대)"),
    SOCIAL_SCIENCE("15호관 (인문대)"),
    CONVENTION("16호관 (컨벤션 센터)"),
    EDUCATION("18호관 (사범대)"),
    SENSE("20호관 (스센)"),
    SPORTS("21호관 (체육관)"),
    SCHOOL_TEAM("22호관 (학교단)"),
    STADIUM("23호관 (강당)"),
    COMPUTER("24호관 (정보대)"),
    ENGINEERING_2("25호관 (공대2)"),
    AI_CONVERGENCE("26호관 (AI융합대)"),
    WELFARE("복지회관"),
    DORMITORY("기숙사"),
    INTERNATIONAL("국제교류관"),
    STUDENT_CENTER("학생회관"),
    LIBRARY("도서관"),
    HAKSAN("학산도서관"),
    LIFE("생명대"),
    CITY("도시대"),
    LAB_1("제1공동실험"),
    LAB_2("제2공동실험");

    private final String label;

    BuildingType(String label) {
        this.label = label;
    }


}

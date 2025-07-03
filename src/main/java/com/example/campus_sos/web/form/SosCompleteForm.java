package com.example.campus_sos.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SosCompleteForm {
    private Long sosRequestId;
    private String helperNickname;
    private String message; // 선택사항
}

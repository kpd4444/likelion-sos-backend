package com.example.campus_sos.domain.sosrequest;

import lombok.Getter;

@Getter
public enum SosStatus {
    IN_PROGRESS("SOS 중"),
    COMPLETED("완료");

    private final String label;

    SosStatus(String label) {
        this.label = label;
    }
}

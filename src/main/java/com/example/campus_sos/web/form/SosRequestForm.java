package com.example.campus_sos.web.form;

import com.example.campus_sos.domain.sosrequest.BuildingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SosRequestForm {
    private BuildingType building;
    private String title;
    private String content;
    private String openChatUrl;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}
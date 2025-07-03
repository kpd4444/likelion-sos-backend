package com.example.campus_sos.web.form;

import com.example.campus_sos.domain.sosrequest.BuildingType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SosUpdateForm {
    private String title;
    private String content;
    private String openChatUrl;
    private BuildingType building;
}
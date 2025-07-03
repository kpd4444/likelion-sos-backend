package com.example.campus_sos.web.form;

import com.example.campus_sos.domain.sosrequest.BuildingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SosRequestForm {
    private BuildingType building;
    private String title;
    private String content;
    private String openChatUrl;
}
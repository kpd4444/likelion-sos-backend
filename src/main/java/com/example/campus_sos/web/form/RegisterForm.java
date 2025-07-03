package com.example.campus_sos.web.form;

import lombok.Data;

@Data
public class RegisterForm {
    private String email;
    private String password;
    private String nickname;
}
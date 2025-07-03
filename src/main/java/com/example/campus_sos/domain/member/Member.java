package com.example.campus_sos.domain.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nickname;

    private int sosPoint;

    @Enumerated(EnumType.STRING)
    private MemberLevel level;

    // 생성자에서 초기값 설정
    public Member(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.sosPoint = 0; // 초기 포인트
        this.level = MemberLevel.getLevelByPoint(this.sosPoint); // 초기 레벨
    }

    // 포인트 변경 시 레벨 자동 갱신
    public void setSosPoint(int point) {
        this.sosPoint = point;
        this.level = MemberLevel.getLevelByPoint(point);
    }

}
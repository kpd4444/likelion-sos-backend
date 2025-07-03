package com.example.campus_sos.web.controller;

import com.example.campus_sos.domain.member.Member;
import com.example.campus_sos.domain.member.MemberRepository;
import com.example.campus_sos.web.form.LoginForm;
import com.example.campus_sos.web.form.MemberForm;
import com.example.campus_sos.domain.member.MemberService;
import com.example.campus_sos.web.form.SosStatusDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    //회원 가입하기
    @PostMapping("/api/signup")
    public ResponseEntity<?> register(@RequestBody MemberForm form) {
        try {
            memberService.save(form);
            return ResponseEntity.ok(Map.of("status", "success", "message", "회원가입 성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "fail", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "fail", "message", "회원 등록 중 오류가 발생했습니다."));
        }
    }

    //멤버들 조회
    @GetMapping("/api/members")
    public ResponseEntity<?> getAllMembers() {
        List<?> members = memberService.findAll();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", members
        ));
    }

    //로그인 기능
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody LoginForm form, HttpSession session) {
        Member member = memberService.login(form.getEmail(), form.getPassword());

        if (member == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", "fail",
                    "message", "이메일 또는 비밀번호가 틀렸습니다."
            ));
        }

        session.setAttribute("loginMember", member.getId());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "로그인 성공",
                "nickname", member.getNickname()
        ));
    }

    //로그아웃인데 혹시 모르니
    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "로그아웃 완료"
        ));
    }


    @GetMapping("/api/sos/my-status")
    public ResponseEntity<?> getMySosStatus(HttpSession session) {
        Long memberId = (Long) session.getAttribute("loginMember");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "fail", "message", "로그인이 필요합니다."));
        }

        SosStatusDto dto = new SosStatusDto(member);
        return ResponseEntity.ok(Map.of("status", "success", "data", dto));
    }
}
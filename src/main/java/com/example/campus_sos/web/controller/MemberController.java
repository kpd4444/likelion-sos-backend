package com.example.campus_sos.web.controller;

import com.example.campus_sos.domain.email.EmailService;
import com.example.campus_sos.domain.email.EmailVerificationService;
import com.example.campus_sos.domain.member.Member;
import com.example.campus_sos.domain.member.MemberRepository;
import com.example.campus_sos.web.form.LoginForm;
import com.example.campus_sos.web.form.MemberForm;
import com.example.campus_sos.domain.member.MemberService;
import com.example.campus_sos.web.form.RegisterForm;
import com.example.campus_sos.web.form.SosStatusDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;

    //회원 가입하기
//    @PostMapping("/api/signup")
//    public ResponseEntity<?> register(@RequestBody MemberForm form) {
//        try {
//            memberService.save(form);
//            return ResponseEntity.ok(Map.of("status", "success", "message", "회원가입 성공"));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(Map.of("status", "fail", "message", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(Map.of("status", "fail", "message", "회원 등록 중 오류가 발생했습니다."));
//        }
//    }
    @PostMapping("/api/register")
    public ResponseEntity<?> register(@RequestBody RegisterForm form) {
        if (memberRepository.existsByEmail(form.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 이메일입니다.");
        }

        // 인증 토큰 생성
        String token = UUID.randomUUID().toString();
        emailVerificationService.saveToken(form.getEmail(), token);

        // 이메일 전송
        String link = "http://localhost:8080/api/verify?email=" + form.getEmail() + "&token=" + token;
        emailService.sendVerificationEmail(form.getEmail(), link);

        return ResponseEntity.ok("인증 메일을 보냈습니다. 메일함을 확인해주세요.");
    }


    @GetMapping("/api/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String email, @RequestParam String token) {
        if (emailVerificationService.isValid(email, token)) {
            emailVerificationService.markAsVerified(email);
            return ResponseEntity.ok("이메일 인증 완료!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 토큰입니다.");
        }
    }

    @PostMapping("/api/complete-register")
    public ResponseEntity<?> completeRegister(@RequestBody RegisterForm form) {
        if (!emailVerificationService.isVerified(form.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이메일 인증이 필요합니다.");
        }

        // 회원 저장
        String email = form.getEmail();       // 폼에서 이메일 가져오기
        String password = form.getPassword(); // 폼에서 비밀번호 가져오기
        String nickname = form.getNickname(); // 폼에서 닉네임 가져오기

        Member member = new Member(email, password, nickname);
        memberRepository.save(member);
        return ResponseEntity.ok("회원가입 완료!");
    }


    @PostMapping("/api/reset-password-request")
    public ResponseEntity<?> sendResetPasswordEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (!memberRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("가입된 이메일이 아닙니다.");
        }

        String token = UUID.randomUUID().toString();
        emailVerificationService.saveToken(email, token);

        String resetLink = "http://localhost:8080/reset-password.html?email=" + email + "&token=" + token;
        emailService.sendVerificationEmail(email, resetLink);

        return ResponseEntity.ok("비밀번호 재설정 메일을 보냈습니다.");
    }

    @PostMapping("/api/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (!emailVerificationService.isValid(email, token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 토큰입니다.");
        }

        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        member.setPassword(newPassword); // setter가 없으면 직접 필드 수정
        memberRepository.save(member);

        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
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
package com.example.campus_sos.web.controller;

import com.example.campus_sos.domain.member.Member;
import com.example.campus_sos.domain.member.MemberRepository;
import com.example.campus_sos.domain.sosrequest.*;
import com.example.campus_sos.web.form.SosCompleteForm;
import com.example.campus_sos.web.form.SosRequestDto;
import com.example.campus_sos.web.form.SosRequestForm;
import com.example.campus_sos.web.form.SosUpdateForm;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class SosRequestController {

    private final SosRequestService sosRequestService;
    private final MemberRepository memberRepository;
    private final SosRequestRepository sosRequestRepository;

    //sos 게시하기
    @PostMapping("/api/sos")
    public ResponseEntity<?> create(@RequestBody SosRequestForm form, HttpSession session) {
        Long memberId = (Long) session.getAttribute("loginMember");
        if (memberId == null) {
            return ResponseEntity.status(401).body(Map.of("status", "fail", "message", "로그인이 필요합니다."));
        }

        try {
            sosRequestService.save(form, memberId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "요청이 등록되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "fail", "message", "요청 등록 중 오류가 발생했습니다."));
        }
    }

    //sos 목록들 보기
    @GetMapping("/api/sos")
    public ResponseEntity<?> getAllRequests() {
        List<SosRequestDto> result = sosRequestService.findAll().stream()
                .map(SosRequestDto::new)
                .toList();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", result
        ));
    }

    //이게 상세 조회
    @GetMapping("/api/sos/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        SosRequestDto dto = sosRequestService.findDtoById(id);

        if (dto == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", "fail",
                    "message", "요청을 찾을 수 없습니다."
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", dto
        ));
    }

    @GetMapping("/api/sos/by-building")
    public ResponseEntity<?> getSosByBuilding(@RequestParam String building) {
        if (building == null || building.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "fail",
                    "message", "건물 이름은 필수입니다."
            ));
        }

        try {
            BuildingType buildingEnum = BuildingType.valueOf(building.toUpperCase());
            List<SosRequestDto> result = sosRequestService.findByBuilding(buildingEnum)
                    .stream()
                    .map(SosRequestDto::new)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", result
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "fail",
                    "message", "존재하지 않는 건물입니다. 정확한 building enum 값을 입력해주세요."
            ));
        }
    }



    //sos 로그인한 멤버가 작성한 게시물 조회
    @GetMapping("/api/sos/my-posts")
    public ResponseEntity<?> getMySosPosts(HttpSession session) {
        Object loginAttr = session.getAttribute("loginMember");

        if (loginAttr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "fail", "message", "로그인이 필요합니다."));
        }

        Member member;


        if (loginAttr instanceof Member) {
            member = (Member) loginAttr;
        }

        else if (loginAttr instanceof Long loginId) {
            member = memberRepository.findById(loginId)
                    .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "fail", "message", "세션에 잘못된 값이 저장되어 있습니다."));
        }

        List<SosRequest> myRequests = sosRequestService.findByRequester(member);
        List<SosRequestDto> dtoList = myRequests.stream()
                .map(SosRequestDto::new)
                .toList();

        return ResponseEntity.ok(Map.of("status", "success", "data", dtoList));
    }

    @PostMapping("/api/sos/complete")
    public ResponseEntity<?> completeSos(@RequestBody SosCompleteForm form, HttpSession session) {
        Object loginAttr = session.getAttribute("loginMember");

        // 로그인 검증
        if (loginAttr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 로그인한 사용자 가져오기
        Member loginMember = (loginAttr instanceof Member)
                ? (Member) loginAttr
                : memberRepository.findById((Long) loginAttr).orElse(null);

        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원 정보를 찾을 수 없습니다.");
        }

        // SOS 요청 조회
        Optional<SosRequest> optionalRequest = sosRequestRepository.findById(form.getSosRequestId());
        if (optionalRequest.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 SOS 요청을 찾을 수 없습니다.");
        }

        SosRequest request = optionalRequest.get();

        // 작성자 검증
        if (!request.getRequester().getId().equals(loginMember.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인이 작성한 요청만 완료할 수 있습니다.");
        }

        // 도움 준 사람 조회
        Optional<Member> optionalHelper = memberRepository.findByNickname(form.getHelperNickname());
        if (optionalHelper.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("도움을 준 사용자를 찾을 수 없습니다.");
        }

        Member helper = optionalHelper.get();

        // 본인에게 포인트 주는 것 방지
        if (request.getRequester().getId().equals(helper.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("본인에게는 SOS 완료 포인트를 줄 수 없습니다.");
        }

        // 포인트 +10 부여
        helper.setSosPoint(helper.getSosPoint() + 10);
        memberRepository.save(helper);

        // SOS 상태 업데이트
        request.setHelper(helper);
        request.setStatus(SosStatus.COMPLETED);
        sosRequestRepository.save(request);

        return ResponseEntity.ok("SOS 완료 처리가 성공적으로 되었습니다.");
    }

    @DeleteMapping("/api/sos/{id}")
    public ResponseEntity<?> deleteSos(@PathVariable Long id, HttpSession session) {
        Object loginAttr = session.getAttribute("loginMember");
        Long loginMemberId = (loginAttr instanceof Member)
                ? ((Member) loginAttr).getId()
                : (Long) loginAttr;

        SosRequest sos = sosRequestRepository.findById(id)
                .orElse(null);

        if (sos == null) {
            return ResponseEntity.status(404).body("게시물을 찾을 수 없습니다.");
        }

        if (!sos.getRequester().getId().equals(loginMemberId)) {
            return ResponseEntity.status(403).body("본인이 작성한 글만 삭제할 수 있습니다.");
        }

        sosRequestRepository.delete(sos);
        return ResponseEntity.ok("삭제 완료");
    }


    @PutMapping("/api/sos/{id}")
    public ResponseEntity<?> updateSos(@PathVariable Long id,
                                       @RequestBody SosUpdateForm form,
                                       HttpSession session) {
        Object loginAttr = session.getAttribute("loginMember");
        Long loginMemberId = (loginAttr instanceof Member)
                ? ((Member) loginAttr).getId()
                : (Long) loginAttr;

        SosRequest sos = sosRequestRepository.findById(id).orElse(null);
        if (sos == null) {
            return ResponseEntity.status(404).body("게시물을 찾을 수 없습니다.");
        }

        if (!sos.getRequester().getId().equals(loginMemberId)) {
            return ResponseEntity.status(403).body("본인이 작성한 글만 수정할 수 있습니다.");
        }

        // 내용 수정
        sos.setTitle(form.getTitle());
        sos.setContent(form.getContent());
        sos.setOpenChatUrl(form.getOpenChatUrl());
        sos.setBuilding(form.getBuilding());

        sosRequestRepository.save(sos);
        return ResponseEntity.ok("수정 완료");
    }


    @GetMapping("/api/sos/helped")
    public ResponseEntity<?> getMyHelpedSosPosts(HttpSession session) {
        Object loginAttr = session.getAttribute("loginMember");

        if (loginAttr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "fail", "message", "로그인이 필요합니다."));
        }

        Member member;

        if (loginAttr instanceof Member) {
            member = (Member) loginAttr;
        } else if (loginAttr instanceof Long loginId) {
            member = memberRepository.findById(loginId)
                    .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "fail", "message", "세션에 잘못된 값이 저장되어 있습니다."));
        }

        // 도움을 준 SOS 요청 목록 조회
        List<SosRequest> helpedRequests = sosRequestService.findByHelper(member);

        List<SosRequestDto> dtoList = helpedRequests.stream()
                .map(SosRequestDto::new)
                .toList();

        return ResponseEntity.ok(Map.of("status", "success", "data", dtoList));
    }




}
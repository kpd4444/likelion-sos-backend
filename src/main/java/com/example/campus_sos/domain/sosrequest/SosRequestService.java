package com.example.campus_sos.domain.sosrequest;

import com.example.campus_sos.domain.member.Member;
import com.example.campus_sos.domain.member.MemberRepository;
import com.example.campus_sos.web.form.SosRequestDto;
import com.example.campus_sos.web.form.SosRequestForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SosRequestService {

    private final SosRequestRepository sosRequestRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void save(SosRequestForm form, Long memberId) {
        // memberId로 Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        // SosRequest 객체 생성 후 저장
        SosRequest request = new SosRequest();
        request.setBuilding(form.getBuilding());
        request.setTitle(form.getTitle());
        request.setContent(form.getContent());
        request.setOpenChatUrl(form.getOpenChatUrl());
        request.setRequester(member);
        request.setCreatedAt(form.getTimestamp());


        sosRequestRepository.save(request);
    }

    public List<SosRequest> findAll() {
        return sosRequestRepository.findAll();
    }


    public SosRequestDto findDtoById(Long id) {
        SosRequest entity = sosRequestRepository.findById(id)
                .orElse(null);
        if (entity == null) return null;
        return new SosRequestDto(entity);
    }

    public List<SosRequest> findByRequester(Member requester) {
        return sosRequestRepository.findAllByRequester(requester);
    }
    public List<SosRequest> findByHelper(Member helper) {
        return sosRequestRepository.findAllByHelper(helper);
    }

    public List<SosRequest> findByBuilding(BuildingType building) {
        return sosRequestRepository.findByBuilding(building);
    }
}
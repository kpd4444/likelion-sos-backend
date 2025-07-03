package com.example.campus_sos.domain.member;

import com.example.campus_sos.web.form.MemberForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void save(MemberForm form) {
        if (memberRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (memberRepository.existsByNickname(form.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        Member member = new Member(form.getEmail(), form.getPassword(), form.getNickname());
        memberRepository.save(member);
    }


    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member login(String email, String password) {
        return memberRepository.findByEmail(email)
                .filter(member -> member.getPassword().equals(password))
                .orElse(null);
    }


}
package com.springboot.member.service;

import com.springboot.auth.utils.CustomAuthorityUtils;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.helper.event.MemberRegistrationApplicationEvent;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils customAuthorityUtils;

    public MemberService(MemberRepository memberRepository, ApplicationEventPublisher publisher, PasswordEncoder passwordEncoder, CustomAuthorityUtils customAuthorityUtils) {
        this.memberRepository = memberRepository;
        this.publisher = publisher;
        this.passwordEncoder = passwordEncoder;
        this.customAuthorityUtils = customAuthorityUtils;
    }
    public Member createMember(Member member){
        verifyExistsEmail(member.getEmail());

        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);

        List<String> roles = customAuthorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);
        Member savedMember = memberRepository.save(member);

        /*publisher.publishEvent(new MemberRegistrationApplicationEvent(this,savedMember));*/
        return savedMember;
    }
    public Member updateMember(Member member){
        Member findMember = findVerifiedMember(member.getMemberId());

        Optional.ofNullable(member.getName())
                .ifPresent(name -> findMember.setName(name));
        Optional.ofNullable(member.getPhone())
                .ifPresent(phone -> findMember.setPhone(phone));
        Optional.ofNullable(member.getMemberStatus())
                .ifPresent(status -> findMember.setMemberStatus(status));
        findMember.setModifiedAt(LocalDateTime.now());

        return memberRepository.save(findMember);
    }
    public Member findMember(long memberId){
        return findVerifiedMember(memberId);
    }
    public Page<Member> findMembers(int page, int size){
        return memberRepository.findAll(PageRequest.of(page, size, Sort.by("memberId").descending()));
    }
    public void deleteMember(long memberId){
        Member findMember = findVerifiedMember(memberId);
        findMember.setMemberStatus(Member.MemberStatus.MEMBER_QUIT);
        // 멤버의 질문들 비활성화
        findMember.disableQuestions();
        memberRepository.save(findMember);
    }
    public Member findVerifiedMember(long memberId){
        Optional<Member> optionalMember =
                memberRepository.findById(memberId);
        Member findMember = optionalMember.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return findMember;
    }
    private void verifyExistsEmail(String email){
        Optional<Member> member = memberRepository.findByEmail(email);
        if(member.isPresent()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }
}

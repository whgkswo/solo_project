package com.springboot.like.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.like.Like;
import com.springboot.like.repository.LikeRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import com.springboot.question.service.QuestionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final QuestionService questionService;
    private final MemberService memberService;

    public LikeService(LikeRepository likeRepository, QuestionService questionService, MemberService memberService) {
        this.likeRepository = likeRepository;
        this.questionService = questionService;
        this.memberService = memberService;
    }

    public Like createLike(Like like, Authentication authentication){
        Member member = memberService.findMember(authentication.getPrincipal().toString());
        like.setMember(member);
        // 같은 멤버가 같은 글에 좋아요를 두 번 누를 수 없음
        Question question = questionService.findVerifiedQuestion(like.getQuestion().getQuestionId());
        for(Like existingLike : question.getLikes()){
            if(existingLike.getMember().getMemberId() == like.getMember().getMemberId()){
                throw new BusinessLogicException(ExceptionCode.LIKE_EXISTS);
            }
        }
        return likeRepository.save(like);
    }
    public void deleteLike(long likeId){
        Like like = findverifiedLike(likeId);
        Question question = like.getQuestion();
        question.removeLike(like);
        likeRepository.delete(like);
    }
    public Like findverifiedLike(long likeId){
        Optional<Like> optionalLike =
                likeRepository.findById(likeId);
        Like findLike = optionalLike.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.LIKE_NOT_FOUND));
        return findLike;
    }
}

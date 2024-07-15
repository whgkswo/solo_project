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
    private final QuestionRepository questionRepository;
    private final MemberService memberService;

    public LikeService(LikeRepository likeRepository, QuestionService questionService, QuestionRepository questionRepository, MemberService memberService) {
        this.likeRepository = likeRepository;
        this.questionService = questionService;
        this.questionRepository = questionRepository;
        this.memberService = memberService;
    }

    public Like createLike(Like like, Authentication authentication){
        Member member = memberService.findMember(authentication.getPrincipal().toString());
        like.setMember(member);

        Question question = questionService.findVerifiedQuestion(like.getQuestion().getQuestionId());
        // 같은 멤버가 같은 글에 좋아요를 두 번 누를 수 없음
        for(Like existingLike : question.getLikes()){
            if(existingLike.getMember().getMemberId() == like.getMember().getMemberId()){
                throw new BusinessLogicException(ExceptionCode.LIKE_EXISTS);
            }
        }
        // question의 좋아요 개수 업데이트
        question.setLikeCount(question.getLikeCount() + 1);
        questionRepository.save(question);

        return likeRepository.save(like);
    }
    public void deleteLike(long likeId){
        Like like = findverifiedLike(likeId);
        Question question = like.getQuestion();
        question.removeLike(like);

        // question의 좋아요 개수 업데이트
        question.setLikeCount(question.getLikeCount() - 1);
        questionRepository.save(question);

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

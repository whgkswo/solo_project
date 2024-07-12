package com.springboot.answer.service;

import com.springboot.answer.entity.Answer;
import com.springboot.answer.repository.AnswerRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import com.springboot.question.service.QuestionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionService questionService;
    private final QuestionRepository questionRepository;
    private final MemberService memberService;

    public AnswerService(AnswerRepository answerRepository, QuestionService questionService, QuestionRepository questionRepository, MemberService memberService) {
        this.answerRepository = answerRepository;
        this.questionService = questionService;
        this.questionRepository = questionRepository;
        this.memberService = memberService;
    }
    public Answer createAnswer(Answer answer, Authentication authentication){
        Member member = memberService.findMember(authentication.getPrincipal().toString());
        answer.setAuthor(member);

        Question question = questionService.findVerifiedQuestion(answer.getQuestion().getQuestionId());

        // 답변은 하나만
        if(question.getAnswer() != null){
            throw new BusinessLogicException(ExceptionCode.ANSWER_EXISTS);
        }

        answer.setQuestion(question);
        return answerRepository.save(answer);
    }
    public Answer updateAnswer(Answer answer, Authentication authentication){
        Answer findAnswer = findVerifiedAnswer(answer.getAnswerId());


        // 답변을 등록한 관리자만 수정 가능
        if(!findAnswer.getAuthor().getEmail().equals(authentication.getPrincipal())){
            throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
        }

        Optional.ofNullable(answer.getContent())
                .ifPresent(content -> findAnswer.setContent(content));
        findAnswer.setModifiedAt(LocalDateTime.now());
        return answerRepository.save(findAnswer);
    }
    public Answer findAnswer(long answerId){
        return findVerifiedAnswer(answerId);
    }
    public Page<Answer> findAnswers(int page, int size){
        return answerRepository.findAll(PageRequest.of(page, size, Sort.by("answerId").descending()));
    }
    public void deleteAnswer(long answerId){
        Answer findAnswer = findVerifiedAnswer(answerId);
        Question question = findAnswer.getQuestion();

        answerRepository.delete(findAnswer);
        question.setAnswer(null);
        questionService.updateQuestion(question);
    }
    public Answer findVerifiedAnswer(long answerId){
        Optional<Answer> optionalAnswer =
                answerRepository.findById(answerId);
        Answer findAnswer = optionalAnswer.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND));
        return findAnswer;
    }
}

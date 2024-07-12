package com.springboot.question.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import com.springboot.member.service.MemberService;
import com.springboot.question.controller.QuestionController;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MemberService memberService;

    public QuestionService(QuestionRepository questionRepository, MemberService memberService) {
        this.questionRepository = questionRepository;
        this.memberService = memberService;
    }

    public Question createQuestion(Question question, Authentication authentication){
        List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        if(roles.contains("ROLE_ADMIN")){
            throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
        }
        Member member = memberService.findMember(authentication.getPrincipal().toString());
        question.setMember(member);
        return questionRepository.save(question);
    }

    public Question updateQuestion(Question question, Authentication authentication){
        Question findQuestion = findVerifiedQuestion(question.getQuestionId());

        // 답변 완료된 질문은 수정 불가능
        if(findQuestion.getQuestionStatus() == Question.QuestionStatus.QUESTION_ANSWERED){
            throw new BusinessLogicException(ExceptionCode.QUESTION_ANSWERED);
        }

        // 질문 수정은 작성자만 가능
        if(!findQuestion.getMember().getEmail().equals(authentication.getPrincipal())){
            throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
        }
        Optional.ofNullable(question.getTitle())
                .ifPresent(title -> findQuestion.setTitle(title));
        Optional.ofNullable(question.getContent())
                .ifPresent(content -> findQuestion.setContent(content));
        Optional.ofNullable(question.getQuestionStatus())
                .ifPresent(status -> findQuestion.setQuestionStatus(status));
        Optional.ofNullable(question.getPublicity())
                .ifPresent(publicity -> findQuestion.setPublicity(publicity));
        Optional.ofNullable(question.getAnswer())
                .ifPresent(answer -> findQuestion.setAnswer(answer));
        findQuestion.setModifiedAt(LocalDateTime.now());

        return questionRepository.save(findQuestion);
    }
    // 관리자 전용 메서드 (관리자만 호출 가능한 deleteAnswer()메서드에서 이 메서드 사용)
    public Question updateQuestion(Question question){
        Question findQuestion = findVerifiedQuestion(question.getQuestionId());
        Optional.ofNullable(question.getTitle())
                .ifPresent(title -> findQuestion.setTitle(title));
        Optional.ofNullable(question.getContent())
                .ifPresent(content -> findQuestion.setContent(content));
        Optional.ofNullable(question.getQuestionStatus())
                .ifPresent(status -> findQuestion.setQuestionStatus(status));
        Optional.ofNullable(question.getPublicity())
                .ifPresent(publicity -> findQuestion.setPublicity(publicity));
        Optional.ofNullable(question.getAnswer())
                .ifPresent(answer -> findQuestion.setAnswer(answer));
        findQuestion.setModifiedAt(LocalDateTime.now());

        return questionRepository.save(findQuestion);
    }
    public Question findQuestion(long questionId, Authentication authentication){
        Question question = findVerifiedQuestion(questionId);

        // 삭제 상태인 질문은 조회 불가능
        if(question.getQuestionStatus() == Question.QuestionStatus.QUESTION_DELETED){
            throw new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND);
        }

        // 프라이빗 질문의 경우
        if(question.getPublicity() == Question.Publicity.PRIVATE){
            List<String> roles = authentication.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .collect(Collectors.toList());
            // 관리자나 작성자 본인만 확인 가능
            if(!roles.contains("ROLE_ADMIN")
                    && !question.getMember().getEmail().equals(authentication.getPrincipal())){
                throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
            }
        }
        question.setViews(question.getViews() + 1);
        return questionRepository.save(question);
    }
    public Page<Question> findQuestions(int page, int size, QuestionController.SortType sortType){
        switch (sortType){
            case VIEWS_ASCENDING:
                return new PageImpl<>(questionRepository.findAll(PageRequest.of(page, size, Sort.by("views"))).stream()
                        .filter(question -> question.getQuestionStatus() != Question.QuestionStatus.QUESTION_DELETED)
                        .collect(Collectors.toList()));
            case VIEWS_DESCENDING:
                return new PageImpl<>(questionRepository.findAll(PageRequest.of(page, size, Sort.by("views").descending())).stream()
                        .filter(question -> question.getQuestionStatus() != Question.QuestionStatus.QUESTION_DELETED)
                        .collect(Collectors.toList()));
            case LIKES_ASCENDING:
                return new PageImpl<>(questionRepository.findAll(PageRequest.of(page, size, Sort.by("likes").descending())).stream()
                        .filter(question -> question.getQuestionStatus() != Question.QuestionStatus.QUESTION_DELETED)
                        .collect(Collectors.toList()));
            case LIKES_DESCENDING:
                return new PageImpl<>(questionRepository.findAll(PageRequest.of(page, size, Sort.by("likes"))).stream()
                        .filter(question -> question.getQuestionStatus() != Question.QuestionStatus.QUESTION_DELETED)
                        .collect(Collectors.toList()));
            default:
                return new PageImpl<>(questionRepository.findAll(PageRequest.of(page, size, Sort.by("questionId").descending())).stream()
                        .filter(question -> question.getQuestionStatus() != Question.QuestionStatus.QUESTION_DELETED)
                        .collect(Collectors.toList()));
        }
    }
    public void deleteQuestion(long questionId, Authentication authentication){

        Question findQuestion = findVerifiedQuestion(questionId);

        // 질문 삭제는 작성자만 가능 (작성자는 무조건 회원)
        if(!findQuestion.getMember().getEmail().equals(authentication.getPrincipal())){
            throw new BusinessLogicException(ExceptionCode.FORBIDDEN);
        }
        // 이미 삭제 상태인 질문은 삭제 불가능
        if(findQuestion.getQuestionStatus() == Question.QuestionStatus.QUESTION_DELETED){
            throw new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND);
        }

        findQuestion.setQuestionStatus(Question.QuestionStatus.QUESTION_DELETED);
        questionRepository.save(findQuestion);
    }
    public Question findVerifiedQuestion(long questionId){
        Optional<Question> optionalQuestion =
                questionRepository.findById(questionId);
        Question findQuestion = optionalQuestion.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
        return findQuestion;
    }
}

package com.springboot.question.controller;

import com.springboot.member.repository.MemberRepository;
import com.springboot.member.service.MemberService;
import com.springboot.question.dto.QuestionDto;
import com.springboot.question.entity.Question;
import com.springboot.question.mapper.QuestionMapper;
import com.springboot.question.service.QuestionService;
import com.springboot.dto.MultiResponseDto;
import com.springboot.dto.SingleResponseDto;
import com.springboot.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/questions")
@Validated
public class QuestionController {
    private final String QUESTION_DEFAULT_URL = "/questions";
    private final QuestionService questionService;
    private final QuestionMapper mapper;
    private final MemberService memberService;

    public QuestionController(QuestionService questionService, QuestionMapper mapper, MemberRepository memberRepository, MemberService memberService) {
        this.questionService = questionService;
        this.mapper = mapper;
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity postQuestion(@Valid @RequestBody QuestionDto.Post questionPostDto,
                                       Authentication authentication){
        Question question = questionService.createQuestion(mapper.questionPostDtoToQuestion(questionPostDto), authentication);

        URI location = UriCreator.createUri(QUESTION_DEFAULT_URL, question.getQuestionId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{question-id}")
    public ResponseEntity patchQuestion(
            @PathVariable("question-id") @Positive long questionId,
            @Valid @RequestBody QuestionDto.Patch questionPatchDto,
            Authentication authentication
    ){
        questionPatchDto.setQuestionId(questionId);
        Question question = questionService.updateQuestion(mapper.questionPatchDtoToQuestion(questionPatchDto), authentication);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.questionToQuestionResponseDto(question)), HttpStatus.OK);
    }
    @GetMapping("/{question-id}")
    public ResponseEntity getQuestion(
            @PathVariable("question-id") @Positive long questionId, Authentication authentication
    ){
        Question question = questionService.findQuestion(questionId, authentication);
        return new ResponseEntity(
                new SingleResponseDto<>(mapper.questionToQuestionResponseDto(question)), HttpStatus.OK
        );
    }
    @GetMapping
    public ResponseEntity getQuestions(@Positive @RequestParam int page,
                                       @Positive @RequestParam int size,
                                       @RequestParam String sortType ){
        Page<Question> pageQuestions = questionService.findQuestions(page-1, size, sortType);
        List<Question> questions = pageQuestions.getContent();

        return new ResponseEntity(
                new MultiResponseDto<>(mapper.questionsToQuestionResponseDto(questions), pageQuestions), HttpStatus.OK
        );
    }
    @DeleteMapping("/{question-id}")
    public ResponseEntity deleteQuestion(
            @PathVariable("question-id") @Positive long questionId, Authentication authentication
    ){
        questionService.deleteQuestion(questionId, authentication);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}

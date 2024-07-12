package com.springboot.answer.controller;

import com.springboot.answer.dto.AnswerDto;
import com.springboot.answer.entity.Answer;
import com.springboot.answer.mapper.AnswerMapper;
import com.springboot.answer.service.AnswerService;
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
@RequestMapping("/answers")
@Validated
public class AnswerController {
    private final String ANSWER_DEFAULT_URL = "/answers";
    private final AnswerService answerService;
    private final AnswerMapper mapper;

    public AnswerController(AnswerService answerService, AnswerMapper mapper) {
        this.answerService = answerService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postAnswer(@Valid @RequestBody AnswerDto.Post answerPostDto){
        Answer answer = answerService.createAnswer(mapper.answerPostToAnswer(answerPostDto));

        URI location = UriCreator.createUri(ANSWER_DEFAULT_URL, answer.getAnswerId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{answer-id}")
    public ResponseEntity patchAnswer(
            @PathVariable("answer-id") @Positive long answerId,
            @Valid @RequestBody AnswerDto.Patch answerPatchDto,
            Authentication authentication
    ){
        answerPatchDto.setAnswerId(answerId);
        Answer answer = answerService.updateAnswer(mapper.answerPatchToAnswer(answerPatchDto), authentication);
        return new ResponseEntity(
                new SingleResponseDto<>(mapper.answerToAnswerResponse(answer)), HttpStatus.OK
        );
    }

    @GetMapping("/{answer-id}")
    public ResponseEntity getAnswer(
            @PathVariable("answer-id") @Positive long answerId
    ){
        Answer answer = answerService.findAnswer(answerId);
        return new ResponseEntity(
                new SingleResponseDto<>(mapper.answerToAnswerResponse(answer)), HttpStatus.OK
        );
    }
    @GetMapping
    public ResponseEntity getAnswers(@Positive @RequestParam int page,
                                     @Positive @RequestParam int size){
        Page<Answer> pageAnswers = answerService.findAnswers(page, size);
        List<Answer> answers = pageAnswers.getContent();

        return new ResponseEntity(
                new MultiResponseDto<>(mapper.answersToAnswerResponses(answers),pageAnswers), HttpStatus.OK
        );
    }

    @DeleteMapping("/{answer-id}")
    public ResponseEntity deleteAnswer(
            @PathVariable("answer-id") @Positive long answerId
    ){
        answerService.deleteAnswer(answerId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}

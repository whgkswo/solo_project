package com.springboot.answer.mapper;

import com.springboot.answer.dto.AnswerDto;
import com.springboot.answer.entity.Answer;
import com.springboot.member.entity.Member;
import com.springboot.question.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnswerMapper {
    default Answer answerPostToAnswer(AnswerDto.Post answerPostDto){
        Answer answer = new Answer();
        Question question = new Question();
        question.setQuestionId(answerPostDto.getQuestionId());
        Member member = new Member();
        member.setMemberId(answerPostDto.getAuthorId());

        answer.setQuestion(question);
        answer.setAuthor(member);
        answer.setContent(answerPostDto.getContent());
        return answer;
    };
    Answer answerPatchToAnswer(AnswerDto.Patch answerPatchDto);
    default AnswerDto.Response answerToAnswerResponse(Answer answer){
        return new AnswerDto.Response(
                answer.getAnswerId(),
                answer.getQuestion().getQuestionId(),
                answer.getAuthor().getMemberId(),
                answer.getContent(),
                answer.getCreatedAt(),
                answer.getModifiedAt()
        );
    }
    default List<AnswerDto.Response> answersToAnswerResponses(List<Answer> answers){
        return answers.stream()
                .map(answer ->
                    new AnswerDto.Response(
                            answer.getAnswerId(),
                            answer.getQuestion().getQuestionId(),
                            answer.getAuthor().getMemberId(),
                            answer.getContent(),
                            answer.getCreatedAt(),
                            answer.getModifiedAt()
                    )
                )
                .collect(Collectors.toList());
    };
}

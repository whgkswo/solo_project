package com.springboot.question.mapper;

import com.springboot.answer.dto.AnswerDto;
import com.springboot.member.entity.Member;
import com.springboot.question.dto.QuestionDto;
import com.springboot.question.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuestionMapper {
    default Question questionPostDtoToQuestion(QuestionDto.Post requestBody){
        Question question = new Question();
        question.setTitle(requestBody.getTitle());
        question.setContent(requestBody.getContent());
        question.setPublicity(requestBody.getPublicity());
        return question;
    };
    Question questionPatchDtoToQuestion(QuestionDto.Patch requestBody);
    default QuestionDto.Response questionToQuestionResponseDto(Question question){
        AnswerDto.Response answerResponse = null;
        if(question.getAnswer() != null){
            answerResponse = new AnswerDto.Response(
                    question.getAnswer().getAnswerId(),
                    question.getQuestionId(),
                    question.getAnswer().getAuthor().getMemberId(),
                    question.getAnswer().getContent(),
                    question.getAnswer().getCreatedAt(),
                    question.getAnswer().getModifiedAt()
            );
        }
        return new QuestionDto.Response(
                question.getQuestionId(),
                question.getMember().getMemberId(),
                question.getTitle(),
                question.getContent(),
                question.getQuestionStatus(),
                question.getPublicity(),
                question.getViews(),
                question.getLikeCount(),
                answerResponse,
                question.getCreatedAt(),
                question.getModifiedAt()
        );
    }
    List<QuestionDto.Response> questionsToQuestionResponseDto(List<Question> questions);
}

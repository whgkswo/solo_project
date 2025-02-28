package com.springboot.question.dto;

import com.springboot.answer.dto.AnswerDto;
import com.springboot.answer.entity.Answer;
import com.springboot.member.entity.Member;
import com.springboot.question.entity.Question;
import com.springboot.validator.NotSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class QuestionDto {
    @Getter
    public static class Post{
        @NotSpace(message = "제목을 작성하세요")
        private String title;

        @NotSpace(message = "본문을 작성하세요")
        private String content;

        @Enumerated(EnumType.STRING)
        private Question.Publicity publicity;
    }

    @Getter
    public static class Patch{
       @Setter
       private long questionId;

       @NotSpace(message = "제목은 공백이 아니어야 합니다.")
       private String title;

       @NotSpace(message = "본문은 공백이 아니어야 합니다.")
       private String content;

       private long answerId;

       private Question.QuestionStatus status;

       private Question.Publicity publicity;
    }

    @AllArgsConstructor
    @Getter
    public static class Response{
        private long questionId;
        private long authorId;
        private String title;
        private String content;
        private Question.QuestionStatus questionStatus;
        private Question.Publicity publicity;
        private int views;
        private int likes;
        private AnswerDto.Response answer;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }
}

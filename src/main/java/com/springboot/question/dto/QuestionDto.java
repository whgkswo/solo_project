package com.springboot.question.dto;

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

public class QuestionDto {
    @Getter
    public static class Post{
        // TODO: 추후 인증,인가 구현 시 작성자는 그쪽에서 처리 요망
        @NotNull(message = "작성자 ID는 필수입니다")
        private long authorId;

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
        private long answerId;
    }
}

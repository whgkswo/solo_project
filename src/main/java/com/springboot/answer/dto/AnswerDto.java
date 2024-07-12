package com.springboot.answer.dto;

import com.springboot.validator.NotSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AnswerDto {
    @Getter
    public static class Post{
        @NotNull
        private long questionId;

        @NotNull
        private long authorId;

        @NotSpace(message = "내용은 공백이 아니어야 합니다.")
        private String content;
    }
    @Getter
    public static class Patch{
        @Setter
        private long answerId;

        @NotSpace(message = "내용은 공백이 아니어야 합니다.")
        private String content;
    }
    @AllArgsConstructor
    @Getter
    public static class Response{
        private long answerId;
        private long questionId;
        private long authorId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }
}

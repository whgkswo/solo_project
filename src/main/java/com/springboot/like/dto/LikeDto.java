package com.springboot.like.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;

public class LikeDto {
    @Getter
    public static class Post{
        @NotNull
        private long questionId;

        @NotNull
        private long memberId;
    }
}

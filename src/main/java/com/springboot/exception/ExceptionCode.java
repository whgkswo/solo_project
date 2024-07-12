package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "Member not found"),
    MEMBER_EXISTS(409, "Member exists"),
    LIKE_EXISTS(409, "Like exists"),
    QUESTION_NOT_FOUND(404, "Question not found"),
    ANSWER_NOT_FOUND(404, "Answer not found"),
    LIKE_NOT_FOUND(404, "Like not found"),
    NOT_IMPLEMENTATION(501, "Not Implementation"),
    INVALID_MEMBER_STATUS(400, "Invalid member status"),
    FORBIDDEN(403, "You are not allowed for requested task"),
    ANSWER_EXISTS(409, "Answer exists"),
    QUESTION_ANSWERED(403, "Answered question cannot be edited")
    ;

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message){
        this.status = code;
        this.message = message;
    }
}

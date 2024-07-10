package com.springboot.member.dto;

import com.springboot.member.entity.Member;
import com.springboot.validator.NotSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

public class MemberDto {
    @Getter
    public static class Post{
        @NotBlank
        @Email
        private String email;

        @NotBlank(message = "이름은 공백이 아니어야 합니다.")
        private String name;

        @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",
                message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다.")
        private String phone;
    }

    @Getter
    public static class Patch{
        @Setter
        private long memberId;

        @NotSpace(message = "회원 이름은 공백이 아니어야 합니다.")
        private String name;

        @NotSpace(message = "휴대폰 번호는 공백이 아니어야 합니다")
        @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",
                message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다")
        private String phone;

        private Member.MemberStatus memberStatus;
    }

    @AllArgsConstructor
    @Getter
    public static class Response{
        private long memberId;
        private String email;
        private String name;
        private String phone;
        private Member.MemberStatus memberStatus;
        private List<Member.Role> roles;
    }
}

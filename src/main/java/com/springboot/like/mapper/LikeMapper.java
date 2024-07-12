package com.springboot.like.mapper;

import com.springboot.like.Like;
import com.springboot.like.dto.LikeDto;
import com.springboot.member.entity.Member;
import com.springboot.question.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    default Like likePostToLike(LikeDto.Post likePostDto){
        Like like = new Like();

        Question question = new Question();
        question.setQuestionId(likePostDto.getQuestionId());
        like.setQuestion(question);

        return like;
    };
}

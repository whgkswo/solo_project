package com.springboot.member.mapper;

import com.springboot.answer.dto.AnswerDto;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.question.dto.QuestionDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {
    Member memberPostToMember(MemberDto.Post requestBody);
    Member memberPatchToMember(MemberDto.Patch requestBody);
    default MemberDto.Response memberToMemberResponse(Member member){
        return new MemberDto.Response(
                member.getMemberId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getMemberStatus(),
                member.getRoles(),
                member.getQuestions().stream()
                        .map(question -> {
                            // 답변 없는 질문이면 답변에 null 넣기
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
                                    question.getLikes().size(),
                                    answerResponse,
                                    question.getCreatedAt(),
                                    question.getModifiedAt()
                            );
                        })
                .collect(Collectors.toList()),
                member.getCreatedAt(),
                member.getModifiedAt()
        );
    };
    List<MemberDto.Response> membersToMemberResponses(List<Member> members);
}

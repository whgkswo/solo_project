package com.springboot.member.entity;

import com.springboot.question.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 13)
    private String phone;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberStatus memberStatus = MemberStatus.MEMBER_ACTIVE;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Question> questions = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, name = "LAST_MODIFIED_AT")
    private LocalDateTime modifiedAt = LocalDateTime.now();

    public void addQuestion(Question question){
        questions.add(question);
        if(question.getMember() == null){
            question.setMember(this);
        }
    }
    public void disableQuestions(){
        questions.stream()
                .forEach(question -> question.setQuestionStatus(Question.QuestionStatus.QUESTION_DEACTIVATED));
    }

    public enum MemberStatus{
        MEMBER_ACTIVE("활동 중"),
        MEMBER_SLEEP("휴면 상태"),
        MEMBER_QUIT("탈퇴 상태")
        ;
        @Getter
        private String description;

        MemberStatus(String description) {
        }
    }
}

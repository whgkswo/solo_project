package com.springboot.answer.entity;

import com.springboot.member.entity.Member;
import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long answerId;

    @OneToOne
    private Question question;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member author;

    @Column(nullable = false, length = 3000)
    private String content = "";

    @Column(nullable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, name = "LAST_MODIFIED_AT")
    LocalDateTime modifiedAt = LocalDateTime.now();

    public void setQuestion(Question question){
        this.question = question;
        if(question.getAnswer() == null){
            question.setAnswer(this);
        }
    }
}

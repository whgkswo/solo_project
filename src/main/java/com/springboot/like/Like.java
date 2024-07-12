package com.springboot.like;

import com.springboot.member.entity.Member;
import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long likeId;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "questionId")
    private Question question;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public void setQuestion(Question question){
        this.question = question;
        if(!question.getLikes().contains(this)){
            question.addLike(this);
        }
    }
    public void removeQuestion(Question question){
        this.question = null;
        if(question.getLikes().contains(this)){
            question.getLikes().remove(this);
        }
    }
}

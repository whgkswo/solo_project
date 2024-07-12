package com.springboot.question.entity;

import com.springboot.answer.entity.Answer;
import com.springboot.like.Like;
import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long questionId;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 3000)
    private String content;

    @Column(nullable = false)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTERED;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Publicity publicity;

    @Column(nullable = false)
    private int views;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();

    @OneToOne(cascade = CascadeType.PERSIST)
    private Answer answer;

    @Column(nullable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, name = "LAST_MODIFIED_AT")
    LocalDateTime modifiedAt = LocalDateTime.now();

    public void addLike(Like like){
        likes.add(like);
        if(like.getQuestion() == null){
            like.setQuestion(this);
        }
    }
    public void removeLike(Like like){
        likes.remove(like);
        if(like.getQuestion() != null){
            like.removeQuestion(this);
        }
    }
    public void setMember(Member member){
        this.member = member;
        if(!member.getQuestions().contains(this)){
            member.getQuestions().add(this);
        }
    }
    public void setAnswer(Answer answer){
        if(answer == null){
            this.answer = null;
            return;
        }
        this.answer = answer;
        questionStatus = QuestionStatus.QUESTION_ANSWERED;
        if(answer.getQuestion() == null){
            answer.setQuestion(this);
        }
    }

    public enum Publicity{
        PUBLIC,
        PRIVATE
        ;
    }

    public enum QuestionStatus{
        QUESTION_REGISTERED,
        QUESTION_ANSWERED,
        QUESTION_DELETED,
        QUESTION_DEACTIVATED
        ;
    }
}

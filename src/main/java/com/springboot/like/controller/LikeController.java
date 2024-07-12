package com.springboot.like.controller;

import com.springboot.like.Like;
import com.springboot.like.dto.LikeDto;
import com.springboot.like.mapper.LikeMapper;
import com.springboot.like.service.LikeService;
import com.springboot.utils.UriCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

@RestController
@RequestMapping("/likes")
@Validated
public class LikeController {
    private final String LIKE_DEFAULT_URL = "/likes";
    private final LikeService likeService;
    private final LikeMapper mapper;

    public LikeController(LikeService likeService, LikeMapper mapper) {
        this.likeService = likeService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postLike(@Valid @RequestBody LikeDto.Post likePostDto,
                                   Authentication authentication){
        Like like = likeService.createLike(mapper.likePostToLike(likePostDto), authentication);

        URI location = UriCreator.createUri(LIKE_DEFAULT_URL, like.getLikeId());
        return ResponseEntity.created(location).build();
    }
    @DeleteMapping("/{like-id}")
    public ResponseEntity deleteLike(@PathVariable("like-id") @Positive long likeId){
        likeService.deleteLike(likeId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}

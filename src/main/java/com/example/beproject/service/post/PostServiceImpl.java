package com.example.beproject.service.post;

import com.example.beproject.domain.post.*;
import com.example.beproject.entity.post.PostEntity;
import com.example.beproject.exception.PostNotFoundException;
import com.example.beproject.exception.PostNotUpdatedException;
import com.example.beproject.repository.post.PostRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
@Builder
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    @Transactional
    public Post createPost(CreatePost post) {
        //Controller에서 createPost로 받았으니까 PostRepository에 Post로 넘겨줘야하니까
        //createPost를 postDTO로 변환하는 작업필요.
        Post newPost = Post.builder()
                .subject(post.getSubject())
                .detail(post.getDetail())
                .writer(post.getWriter())
                .tag(post.getTag())
                .status(PostStatus.NEW)
                .build();

        return postRepository.createPost(newPost);
    }


    /*
    @Override
    @Transactional
    public Post updatePost(Long id, UpdatePost updatePost) {
        //id에 해당하는 원래 게시글 찾아와서 업데이트해주기
        Optional<Post> isExistingPost =  postRepository.findById(id);
        //존재하는지 확인 후 업뎃하고 repository에 저장
        if (isExistingPost.isPresent()) {
            Post existingPost = isExistingPost.get().toDTO();

            //존재하는 게시물에 update해주기
            existingPost = Post.builder()
                    .id(?????)
                    .subject(updatePost.getSubject())
                    .detail(updatePost.getDetail())
                    .tag(updatePost.getTag())
                    .status(PostStatus.MODIFIED)
                    .build();

            //수정한 게시물을 repository에 저장하기
            return  postRepository.updatePost(existingPost);

        } else {
            return null;
        }

    }

    */


    @Override
    public Post updatePost(Long id, UpdatePost updatePost) {
        // id로 게시글 정보를 조회
        Optional<Post> optionalPost = postRepository.findById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            // 게시글 정보 업데이트
            post.updatePost(updatePost.getSubject(), updatePost.getDetail(), updatePost.getTag());

            // title, detail이 빈 값인 경우 업데이트 하지 않음
            String newSubject = updatePost.getSubject();
            String newDetail = updatePost.getDetail();

            if (newSubject != null && !newSubject.isEmpty() && newDetail != null && !newDetail.isEmpty()) {
                throw new PostNotUpdatedException();
            }

            // 게시글 정보 저장 및 반환
            return postRepository.createPost(post);

        }
        // 조회한 게시글이 존재하지 않는 경우 null 반환
        return null;
    }


}


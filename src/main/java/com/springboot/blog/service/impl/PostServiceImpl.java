package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    private ModelMapper mapper;

    // Only one constructor, can omit autowired
    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper) {

        this.postRepository = postRepository;
        this.mapper = mapper;
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        // convert DTO to entity
//        Post post = new Post();
//        post.setTitle(postDto.getTitle());
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getContent());
//        Post newPost = postRepository.save(post);

        // How come we do not check whether the fields are null.
        // We know that there is frontend validation and backend validation.
        // Backend validation can either be done in the service layer or it can be done using entity validation
        // Service layer. If this is null, do not create. But the fields can actually be null and it will not cause errors during conversion from DTO to entity.
        Post post = mapToEntity(postDto);
        Post newPost = postRepository.save(post);

        // convert entity to DTO
        // Technically you do not need a new postResponse. Can use postDto.
//        PostDto postResponse = new PostDto();
//        postResponse.setId(newPost.getId());
//        postResponse.setTitle(newPost.getTitle());
//        postResponse.setDescription(newPost.getDescription());
//        postResponse.setContent(newPost.getContent());
        PostDto postResponse = mapToDTO(newPost);
        return postResponse;

//        Post post = new Post();
//        post.setTitle(postDto.getTitle());
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getContent());
//
//        Post newPost = postRepository.save(post);
//
//        //PostDto postResponse = new PostDto();
//        postDto.setId(newPost.getId());
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
        // Create Sort Object
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        // Create Pageable instance
        // PageRequest.of() can take in pageNo, pageSize and Sort object
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // Get Page from Repo
        Page<Post> posts = postRepository.findAll(pageable);

        // get content for page object
        List<Post> listOfPosts = posts.getContent();
        List<PostDto> content = listOfPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        // Mapping it to PostResponse
        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());
        return postResponse;
    }

    // Version 3: Implemented pagination without sorting
//    @Override
//    public PostResponse getAllPosts(int pageNo, int pageSize) {
//        // Create Pageable instance
//        Pageable pageable = PageRequest.of(pageNo, pageSize);
//
//        // Get Page from Repo
//        Page<Post> posts = postRepository.findAll(pageable);
//
//        // get content for page object
//        List<Post> listOfPosts = posts.getContent();
//        List<PostDto> content = listOfPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
//
//        // Mapping it to PostResponse
//        PostResponse postResponse = new PostResponse();
//        postResponse.setContent(content);
//        postResponse.setPageNo(posts.getNumber());
//        postResponse.setPageSize(posts.getSize());
//        postResponse.setTotalElements(posts.getTotalElements());
//        postResponse.setTotalPages(posts.getTotalPages());
//        postResponse.setLast(posts.isLast());
//        return postResponse;
//    }

    // Version 2
//    @Override
//    public List<PostDto> getAllPosts(int pageNo, int pageSize) {
//        // Create Pageable instance
//        Pageable pageable = PageRequest.of(pageNo, pageSize);
//
//        // Get Page from Repo
//        Page<Post> posts = postRepository.findAll(pageable);
//
//        // get content for page object
//        List<Post> listOfPosts = posts.getContent();
//        return listOfPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
//    }

    // Version 1
//    @Override
//    public List<PostDto> getAllPosts(int pageNo, int pageSize) {
//        List<Post> posts = postRepository.findAll();
//        return posts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
//    }

    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return mapToDTO(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        // Eugene: Do we need to set all the fields? Or check for null?
        // Eugene: I think when we save we need to check for null result when saving.
        // Eugene: Minor point but I was wondering whether I could do
        // postRepository.save(post);
        // return mapToDTO(post);
        //OR
        //Post updatedPost = postRepository.save(post);
        //return mapToDTO(updatedPost);

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        Post updatedPost = postRepository.save(post);
        return mapToDTO(updatedPost);
    }

    @Override
    public void deletePostById(long id) {
        // check whether post exists first
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        postRepository.delete(post);

        // ALternatively
//        boolean exists = postRepository.existsById(id);
//        if (!exists) {
//            throw new ResourceNotFoundException("Post", "id", id);
//        }
//        postRepository.deleteById(id);
    }

    // convert Entity to DTO
    private PostDto mapToDTO(Post post) {
        PostDto postDto = mapper.map(post,PostDto.class);


            // Manual Mapping
//        PostDto postDto = new PostDto();
//        postDto.setId(post.getId());
//        postDto.setTitle(post.getTitle());
//        postDto.setDescription(post.getDescription());
//        postDto.setContent(post.getContent());
        return postDto;
    }

    // convert Dto to Entity
    private Post mapToEntity(PostDto postDto) {
        Post post = mapper.map(postDto, Post.class);

        // Manual Mapping
//        Post post = new Post();
//        post.setTitle(postDto.getTitle());
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getContent());
        return post;
    }
}

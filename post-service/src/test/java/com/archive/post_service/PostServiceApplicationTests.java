package com.archive.post_service;

import static org.junit.jupiter.api.Assertions.*;

import com.archive.post_service.dto.AddNewPostDto;
import com.archive.post_service.repositories.PostRepository;
import com.archive.post_service.services.PostService;
import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


@SpringBootTest
@AutoConfigureMockMvc
class PostServiceApplicationTests {
	@InjectMocks
	private PostService postService;
	@Mock
	private PostRepository postRepository;
	@Mock
	private Cloudinary cloudinary;
	private AddNewPostDto addNewPostDto;
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		addNewPostDto = new AddNewPostDto();
	}

	@Test
	void shouldReturnMinus1WhenTitleIsEmpty() throws ExecutionException, InterruptedException, TimeoutException {
		addNewPostDto.setTitle("");
		int result = postService.addNewPost(addNewPostDto);
		assertEquals(-1, result);
	}

	@Test
	void shouldReturnMinus2WhenImageIsNull() throws ExecutionException, InterruptedException, TimeoutException {
		addNewPostDto.setTitle("Test title");
		addNewPostDto.setImage(null);
		int result = postService.addNewPost(addNewPostDto);
		assertEquals(-2, result);
	}

}

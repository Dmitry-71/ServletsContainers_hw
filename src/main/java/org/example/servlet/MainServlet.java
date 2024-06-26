package org.example.servlet;

import org.example.controller.PostController;
import org.example.exception.NotFoundException;
import org.example.repository.PostRepository;
import org.example.service.PostService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {
    PostController postController;
    public static final String API_POSTS = "/api/posts";
    public static final String API_POSTS_ID = "/api/posts/\\d+";
    public static final String STR = "/";
    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    public static final String DELETE_METHOD = "DELETE";

    @Override
    public void init() throws ServletException {
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        postController = new PostController(service);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse response) {
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();


            if (method.equals(GET_METHOD) && path.equals(API_POSTS)) {
                postController.all(response);
                return;
            }
            if (method.equals(GET_METHOD) && path.matches(API_POSTS_ID)) {
                final var id = Long.parseLong(path.substring(path.lastIndexOf(STR) + 1));
                postController.getById(id, response);
                return;
            }
            if (method.equals(POST_METHOD) && path.equals(API_POSTS)) {
                postController.save(req.getReader(), response);
            }
            if (method.equals(DELETE_METHOD) && path.matches(API_POSTS_ID)) {
                final var id = Long.parseLong(path.substring(path.lastIndexOf(STR) + 1));
                postController.removeById(id, response);
            }
        } catch (NotFoundException e) {
            e.getMessage();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException ioException) {
            ioException.getMessage();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

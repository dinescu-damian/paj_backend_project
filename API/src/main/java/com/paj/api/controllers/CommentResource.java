package com.paj.api.controllers;

import com.paj.api.entities.Comment;
import com.paj.api.services.CommentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/comments")
public class CommentResource {

    @Inject
    private CommentService commentService;

    // Get comment by id. Example: /comments/1
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Comment getCommentById(@PathParam("id") String id) {
        return commentService.findCommentById(Long.parseLong(id));
    }

    // Get all comments by trip id. The trip id is passed as a query parameter. Example: /comments?tripId=1
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Comment> getAllCommentsByTripId(@QueryParam("tripId") String tripId) {
        return commentService.getAllCommentsByTripId(Long.parseLong(tripId));
    }

    // Save trip
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void saveComment(Comment comment) {
        commentService.saveComment(comment);
    }

    // Update trip
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateComment(Comment comment) {
        commentService.updateComment(comment);
    }

    // Delete trip
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteComment(Comment comment) {
        commentService.deleteComment(comment);
    }
}

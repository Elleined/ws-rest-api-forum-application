package com.forum.application.mapper;

import com.forum.application.dto.CommentDTO;
import com.forum.application.dto.MentionDTO;
import com.forum.application.dto.PostDTO;
import com.forum.application.dto.notification.CommentNotificationResponse;
import com.forum.application.dto.notification.NotificationResponse;
import com.forum.application.dto.notification.ReplyNotificationResponse;
import com.forum.application.exception.ResourceNotFoundException;
import com.forum.application.model.Type;
import com.forum.application.model.User;
import com.forum.application.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapper {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final MentionService mentionService;
    private final MentionMapper mentionMapper;

   public NotificationResponse toCommentNotification(int postId, int commenterId) throws ResourceNotFoundException {
        final PostDTO postDTO = postService.getById(postId);
        final User commenter = userService.getById(commenterId);

        boolean isModalOpen = userService.isModalOpen(postDTO.getAuthorId(), postId, Type.COMMENT);
        int count = commentService.getNotificationCountForRespondent(postDTO.getAuthorId(), postId, commenterId);
        return CommentNotificationResponse.builder()
                .id(postId)
                .message(commenter.getName() + " commented in your post: " + "\"" + postDTO.getBody() + "\"")
                .respondentPicture(commenter.getPicture())
                .respondentId(commenterId)
                .uri("/posts/" + postId + "/comments")
                .type(Type.COMMENT)
                .isModalOpen(isModalOpen)
                .count(count)
                .build();
    }

    public NotificationResponse toReplyNotification(int commentId, int replierId) throws ResourceNotFoundException {
        final CommentDTO commentDTO = commentService.getById(commentId);
        final User replier = userService.getById(replierId);

        boolean isModalOpen = userService.isModalOpen(commentDTO.getCommenterId(), commentId, Type.REPLY);
        int count = replyService.getNotificationCountForRespondent(commentDTO.getCommenterId(), commentId, replierId);
        return ReplyNotificationResponse.replyNotificationBuilder()
                .id(commentId)
                .message(replier.getName() + " replied to your comment: " +  "\"" + commentDTO.getBody() + "\"")
                .respondentPicture(replier.getPicture())
                .respondentId(replierId)
                .uri("/posts/comments/" + commentId + "/replies")
                .commentURI("/posts/" + commentDTO.getPostId() + "/comments")
                .type(Type.REPLY)
                .count(count)
                .isModalOpen(isModalOpen)
                .build();
    }

    public MentionDTO toMentionNotification(int mentionId) {
        MentionDTO mentionDTO = mentionMapper.toDTO(mentionService.getById(mentionId));
        User mentioningUser = userService.getById(mentionDTO.getMentioningUserId());
        String message = switch (Type.valueOf(mentionDTO.getType())) {
            case POST -> mentioningUser.getName() + " mention you in a post: " + "\"" + postService.getById(mentionDTO.getTypeId()).getBody() + "\"";
            case COMMENT -> mentioningUser.getName() + " mention you in a comment " + "\"" + commentService.getById(mentionDTO.getTypeId()).getBody() + "\"";
            case REPLY -> mentioningUser.getName() + " mention you in a reply " + "\"" + replyService.getById(mentionDTO.getTypeId()).getBody() + "\"";
        };
        mentionDTO.setMessage(message);
        return mentionDTO;
    }
}
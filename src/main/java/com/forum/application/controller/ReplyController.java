package com.forum.application.controller;

import com.forum.application.dto.ReplyDTO;
import com.forum.application.model.Type;
import com.forum.application.service.ForumService;
import com.forum.application.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/forum/api/posts/comments/{commentId}/replies")
public class ReplyController {

    private final ForumService forumService;
    private final UserService userService;

    @GetMapping
    public List<ReplyDTO> getAllRepliesOf(@PathVariable("commentId") int commentId) {
        return forumService.getAllRepliesOf(commentId);
    }

    @GetMapping("/{replyId}")
    public ReplyDTO getById(@PathVariable("replyId") int replyId) {
        return forumService.getReplyById(replyId);
    }

    @PostMapping
    public ResponseEntity<?> saveReply(@PathVariable("commentId") int commentId,
                                       @RequestParam("body") String body,
                                       @RequestParam(required = false, name = "mentionedUserIds") Set<Integer> mentionedUserIds,
                                       HttpSession session) {

        if (forumService.isEmpty(body)) return ResponseEntity.badRequest().body("Reply body cannot be empty!");
        if (forumService.isPostCommentSectionClosedByCommentId(commentId)) return ResponseEntity.badRequest().body("Cannot reply to this comment because author already closed the comment section for this post!");
        if (forumService.isCommentDeleted(commentId)) return ResponseEntity.badRequest().body("The comment you trying to reply is either be deleted or does not exists anymore!");

        int currentUserId = userService.getCurrentUser().getId();

        int commenterId = forumService.getCommentById(commentId).getCommenterId();
        if (userService.isYouBeenBlockedBy(currentUserId, commenterId)) return ResponseEntity.badRequest().body("Cannot reply because this user block you already!");

        int replyId = forumService.saveReply(currentUserId, commentId, body);
        if (mentionedUserIds != null) forumService.mentionUsers(currentUserId, mentionedUserIds, Type.REPLY, replyId); // might be bug because if post doesnt get stored this will be null
        ReplyDTO replyDTO = forumService.getReplyById(replyId);
        return ResponseEntity.ok(replyDTO);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<ReplyDTO> delete(@PathVariable("replyId") int replyId) {
        forumService.deleteReply(replyId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/body/{replyId}")
    public ResponseEntity<ReplyDTO> updateReplyBody(@PathVariable("replyId") int replyId,
                                             @RequestParam("newReplyBody") String newReplyBody) {

        forumService.updateReplyBody(replyId, newReplyBody);

        ReplyDTO replyDTO = forumService.getReplyById(replyId);
        return ResponseEntity.ok(replyDTO);
    }
}

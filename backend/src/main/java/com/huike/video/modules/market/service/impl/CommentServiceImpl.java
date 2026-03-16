package com.huike.video.modules.market.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.market.dto.CommentDTO;
import com.huike.video.modules.market.entity.Comment;
import com.huike.video.modules.market.mapper.CommentMapper;
import com.huike.video.modules.market.service.CommentService;
import com.huike.video.modules.market.vo.CommentVO;
import com.huike.video.modules.user.entity.User;
import com.huike.video.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    @Override
    public Page<CommentVO> getComments(String targetId, int page, int size) {
        // 先分页查一级评论
        Page<Comment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getTargetId, targetId)
               .eq(Comment::getParentId, 0L)
               .orderByDesc(Comment::getCreatedAt);

        Page<Comment> commentPage = commentMapper.selectPage(pageParam, wrapper);

        // 收集一级评论ID，批量查子回复
        List<Long> rootIds = commentPage.getRecords().stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        Map<Long, List<Comment>> repliesMap = Map.of();
        if (!rootIds.isEmpty()) {
            List<Comment> allReplies = commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                    .in(Comment::getParentId, rootIds)
                    .orderByAsc(Comment::getCreatedAt));
            repliesMap = allReplies.stream().collect(Collectors.groupingBy(Comment::getParentId));
        }

        // 组装 VO
        Map<Long, List<Comment>> finalRepliesMap = repliesMap;
        List<CommentVO> voList = commentPage.getRecords().stream().map(c -> {
            CommentVO vo = toVO(c);
            List<Comment> replies = finalRepliesMap.getOrDefault(c.getId(), List.of());
            vo.setReplies(replies.stream().map(this::toVO).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());

        Page<CommentVO> result = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public Long addComment(CommentDTO dto) {
        String userId = StpUtil.getLoginIdAsString();

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setTargetId(dto.getTargetId());
        comment.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        comment.setContent(dto.getContent());
        comment.setLikeCount(0);
        commentMapper.insert(comment);
        return comment.getId();
    }

    @Override
    public Boolean deleteComment(Long commentId) {
        String userId = StpUtil.getLoginIdAsString();
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(10030, "评论不存在");
        }
        // 越权校验: 仅本人可删(管理员另开接口)
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(10031, "无权删除他人评论");
        }
        commentMapper.deleteById(commentId);
        return true;
    }

    private CommentVO toVO(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setCommentId(comment.getId());
        vo.setUserId(comment.getUserId());
        vo.setContent(comment.getContent());
        vo.setLikeCount(comment.getLikeCount());
        vo.setCreatedAt(comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null);
        vo.setReplies(new ArrayList<>());

        // 查用户基本信息
        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            vo.setNickname(user.getUsername());
            vo.setAvatar(user.getAvatarUrl());
        }
        return vo;
    }
}

package com.nxsp.community.service;

import com.nxsp.community.dao.DiscussPostMapper;
import com.nxsp.community.entity.DiscussPost;
import com.nxsp.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }
    public int addDiscussPost(DiscussPost post){
        if(post==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        // 转义HTML标记(恶意注册的时候，会使用诸如 <script>alert('papapa')</script>，转义标签)
        //敏感词过滤
        //转义HTML标记
        post.setTitle(sensitiveFilter.filter(HtmlUtils.htmlEscape(post.getTitle())));
        post.setContent(sensitiveFilter.filter(HtmlUtils.htmlEscape(post.getContent())));
        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

}

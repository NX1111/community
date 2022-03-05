package com.nxsp.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nxsp.community.dao.DiscussPostMapper;
import com.nxsp.community.entity.DiscussPost;
import com.nxsp.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    //Caffeine核心接口：Cache，LoadingCache,AsynLoadingCache

    //帖子列表的缓存（key-value形式）
    private LoadingCache<String,List<DiscussPost>> postListCache;

    //帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    // 被注解的方法，在对象加载完依赖注入后执行
    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存
        // postListCache.get(key)调用此方法，此方法执行过一次后，下次执行从缓存中取数据
        postListCache = Caffeine.newBuilder()
        .maximumSize(maxSize)
        .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                //build返回LoadingCache，
        .build(new CacheLoader<String, List<DiscussPost>>() {
            @Nullable
            @Override
            public List<DiscussPost> load(@NonNull String key) throws Exception {
                    if(key == null|| key.length() == 0){
                        throw new IllegalArgumentException("参数错误！");
                    }
                    String[] params = key.split(":");
                    if(params == null || params.length != 2){
                       throw new IllegalArgumentException("参数错误！");
                    }
                    int offset = Integer.valueOf(params[0]);
                    int limit = Integer.valueOf(params[1]);

                    //二级缓存：Redis -> mysql

                logger.debug("load post list from DB.");
                return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
            }
        });

        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        logger.debug("load post rows from DB.");
                        return discussPostMapper.selectDiscussPostRows(0);
                    }
                });





    }
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int mode) {
        // 查询全部时且按照热帖排序时启用缓存
        if (userId == 0 && mode == 1) {
            return postListCache.get(offset + ":" + limit);
        }
        logger.debug("load post list from DB.");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit , mode);
    }

    public int findDiscussPostRows(int userId) {
        // 查询全部时且按照热帖排序时启用缓存
        if (userId == 0) {
            return postRowsCache.get(userId);
        }
        logger.debug("load post Rows from DB.");
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

    public int updateType(int id , int type){
        return discussPostMapper.updateType(id,type);
    }

    public int updateStatus(int id , int status){
        return discussPostMapper.updateStatus(id , status);
    }


    public int  updateScore(int postId, double  score) {
        return discussPostMapper.updateScore(postId,score);
    }
}

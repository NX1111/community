package com.nxsp.community.dao;

import com.nxsp.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);



    /**
     * 更新回帖总数量
     * @param id post_id 发布帖子的自身id
     * @param commentCount 这个帖子的回帖总数量，包括回复
     * @return
     */
    int updateCommentCount(int id, int commentCount);


}

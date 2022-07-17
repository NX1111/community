package com.nxsp.community.service;

import com.nxsp.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * Description: 点赞功能，取消点赞
     *
     * @param userId:     点赞者id
     * @param entityType: 待点赞的实体类型
     * @param entityId:   待点赞的实体Id
     * @patam entityUserId: 实体拥有者的id
     * @return void:
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 生成当前实体的点赞在Redis中的key
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                // 判断Redis中是否有此用户对当前实体的点赞(集合set)
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                //开启事务
                operations.multi();
                if (isMember) {
                    // 有点赞，双击取消点赞
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    // 没有点赞，记录点赞
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                //执行事务
                return operations.exec();
            }
        });
    }

    /**
     * Description: 查询某个实体点赞的数量
     *
     * @param entityType:
     * @param entityId:
     * @return long:
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        // 生成当前实体的点赞在Redis中的key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * Description: 查询某人对某实体的点赞状态
     *
     * @param userId:
     * @param entityType:
     * @param entityId:
     * @return int: 1--已点赞；0--未点赞
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    // 查询某个用户获得的赞
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }

}

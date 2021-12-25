package com.nxsp.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    /**
     * Description: 生成随机字符串 UUID：Universally unique identifier通用唯一识别码
     * @return java.lang.String:
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5加密
    // hello -> abc123def456
    // hello + 3e4a8 -> abc123def456abc
    /**
     * Description: MD5加密（只能加密不能解密，相同密码加密结果相同）
     *              防止拖库撞库（密码加随机字符串再进行加密）
     * @param key: 原始密码
     * @return java.lang.String: 加密密码
     */
    public static String md5(String key) {
        //null "" 都认为是空
        if (StringUtils.isBlank(key)) {
            return null;
        }
        //spring加密工具类
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJSONString(int code , String msg , Map<String , Object> map){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

}

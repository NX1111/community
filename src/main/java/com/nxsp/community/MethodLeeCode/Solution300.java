package com.nxsp.community.MethodLeeCode;

import org.junit.Test;

import java.util.Arrays;

public class Solution300 {
    // Dynamic programming.
        public int lengthOfLIS(int[] nums) {
            if(nums.length == 0) return 0;
            int[] dp = new int[nums.length];
            int res = 0;
            Arrays.fill(dp, 1);
            for(int i = 0; i < nums.length; i++) {
                for(int j = 0; j < i; j++) {
                    if(nums[j] < nums[i]) dp[i] = Math.max(dp[i], dp[j] + 1);
                }
                res = Math.max(res, dp[i]);
            }
            return res;
        }

       @Test
    public void test(){
            int[] nums = {10,9,2,5,3,7,21,18};
           System.out.println(lengthOfLIS(nums));
    }

}

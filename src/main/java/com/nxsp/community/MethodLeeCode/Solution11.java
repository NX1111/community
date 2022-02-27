package com.nxsp.community.MethodLeeCode;

import org.junit.Test;

public class Solution11 {
    public int maxArea(int[] height){
        if(height.length <= 1) return 0;
        int  i = 0 , j = height.length-1 , res = 0 ;
        res = Math.min(height[i],height[j])*(j-i);
        while(i<j){
            if(height[i]<=height[j]){
                i++;
                res = Math.max(res,Math.min(height[i],height[j])*(j-i));
            }else {
                j--;
                res = Math.max(res,Math.min(height[i],height[j])*(j-i));
            }
        }
        return res;
    }

    @Test
    public void test(){
        int[] height = {1,8,6,2,5,4,8,3,7};
        System.out.println(maxArea(height));
    }
}

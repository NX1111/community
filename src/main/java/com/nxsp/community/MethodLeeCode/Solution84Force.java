package com.nxsp.community.MethodLeeCode;

import org.junit.Test;

import java.util.Stack;

public class Solution84Force {

    public int largestRectangleArea(int[] heights){
        // 初始化最终结果为0
        int res = 0;
        Stack<Integer> stack = new Stack<>();

        // 将给定的原数组左右各添加一个元素0
        int[] newHeights = new int[heights.length + 2];
        newHeights[0] = 0;
        newHeights[newHeights.length-1] = 0;
        for (int i = 1; i < heights.length + 1; i++) {
            newHeights[i] = heights[i - 1];
        }

        // 开始遍历
        for (int i = 0; i < newHeights.length; i++) {
            // 如果栈不为空且当前考察的元素值小于栈顶元素值，
            // 则表示以栈顶元素值为高的矩形面积可以确定
            while (!stack.isEmpty() && newHeights[i] < newHeights[stack.peek()]) {
                // 弹出栈顶元素
                int cur = stack.pop();
                // 获取栈顶元素对应的高
                int curHeight = newHeights[cur];

                // 栈顶元素弹出后，新的栈顶元素就是其左侧边界
                int leftIndex = stack.peek();
                // 右侧边界是当前考察的索引
                int rightIndex = i;
                // 计算矩形宽度
                int curWidth = rightIndex - leftIndex - 1;

                // 计算面积
                res = Math.max(res, curWidth * curHeight);
            }

            // 当前考察索引入栈
            stack.push(i);
        }

        return res;

    }

    @Test
    public void test(){
        int[] nums = {2,1,5,6,2,3};
        System.out.println(largestRectangleArea(nums));
    }
}

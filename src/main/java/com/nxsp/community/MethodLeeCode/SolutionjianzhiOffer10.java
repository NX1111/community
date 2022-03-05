package com.nxsp.community.MethodLeeCode;

import org.junit.Test;

public class SolutionjianzhiOffer10 {
    public int game(int n){
       int a = 1 ,b = 2, sum = 0;
        for(int i = 0 ; i < n-1 ; i++){
            sum = (a+b)%1000000007;
            a = b;
            b = sum;
        }
        return a;
    }

    @Test
    public void test(){
        System.out.println(game(7));
    }
}

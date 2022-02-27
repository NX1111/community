package com.nxsp.community.MethodLeeCode;

import lombok.val;
import org.junit.Test;

public class Solution64 {
    public int minTrace(int[][] matric){
        if(matric == null) return 0;
        int m = matric.length;
        int n = matric[0].length;
        for(int i =0 ; i<m ; i++ ){
            for(int j = 0 ; j < n ; j++){
                if(i == 0 && j == 0) continue;
                if(i == 0 && j != 0) matric[i][j] = matric[i][j-1] + matric[i][j];
                if(i != 0 && j == 0) matric[i][j] = matric[i-1][j] + matric[i][j];
                if(i != 0 && j != 0) matric[i][j] =Math.min(matric[i][j-1],matric[i-1][j]) + matric[i][j];
            }
        }
        return matric[m-1][n-1];
    }

    @Test
    public void test(){
        int[][] matric =  {{1,3,1},{1,5,1},{4,2,1}};
        System.out.println(minTrace(matric));
    }


}

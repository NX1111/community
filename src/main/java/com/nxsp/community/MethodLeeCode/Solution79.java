package com.nxsp.community.MethodLeeCode;

public class Solution79 {
    private boolean find;  // 定义为成员变量，方便以下两个成员方法使用和修改

    public boolean exist(char[][] board, String word) {
        if (board == null) return false;
        int m = board.length, n = board[0].length;
        boolean[][] visited = new boolean[m][n];
        find = false;

        for (int i = 0; i < m; i++){
            for (int j = 0; j < n; j++){
                backtracking(i, j, board, word, visited, 0);  // 从左上角开始遍历棋盘每个格子
            }
        }
        return find;
    }

    /**
     * i,j,board：棋盘格及当前元素的坐标
     * word: 要搜索的目标单词
     * visited：记录当前格子是否已被访问过
     * pos: 记录目标单词的字符索引，只有棋盘格字符和pos指向的字符一致时，才有机会继续搜索接下来的字符；如果pos已经过了目标单词的尾部了，那么便说明找到目标单词了
     */
    public void backtracking(int i, int j, char[][] board, String word, boolean[][] visited, int pos){
        // 超出边界、已经访问过、已找到目标单词、棋盘格中当前字符已经和目标字符不一致了
        if(i<0 || i>= board.length || j<0 || j >= board[0].length || visited[i][j] || find
                || board[i][j]!=word.charAt(pos))  return;

        if(pos == word.length()-1){
            find = true;
            return;
        }

        visited[i][j] = true;  // 修改当前节点状态
        backtracking(i+1, j, board, word, visited, pos+1);  // 遍历子节点
        backtracking(i-1, j, board, word, visited, pos+1);
        backtracking(i, j+1, board, word, visited, pos+1);
        backtracking(i, j-1, board, word, visited, pos+1);
        visited[i][j] = false; // 撤销修改,如果所有的路径都不满足，需要将棋盘状态设为初始状态，防止与其他的路径有干涉
    }

}

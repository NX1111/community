package com.nxsp.community.MethodLeeCode;


import org.junit.Test;

public class Solution226 {

    public TreeNode invertTree(TreeNode root) {
        if(root==null) {
            return null;
        }
        //下面三句是将当前节点的左右子树交换
        TreeNode tmp = root.right;
        root.right = root.left;
        root.left = tmp;
        //递归交换当前节点的 左子树
        invertTree(root.left);
        //递归交换当前节点的 右子树
        invertTree(root.right);
        //函数返回时就表示当前这个节点，以及它的左右子树
        //都已经交换完了
        return root;
    }



    @Test
    public void test(){
        TreeNode treeNode1 = new TreeNode(1);
        TreeNode treeNode3 = new TreeNode(3);
        TreeNode treeNode6 = new TreeNode(6);
        TreeNode treeNode9 = new TreeNode(9);
        TreeNode treeNode2 = new TreeNode(2,treeNode1,treeNode3);
        TreeNode treeNode7 = new TreeNode(7,treeNode6,treeNode9);
        TreeNode treeNode4 = new TreeNode(4,treeNode2,treeNode7);
       TreeNode a = invertTree(treeNode4);


    }

}



 class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode() {}
      TreeNode(int val) { this.val = val; }
      TreeNode(int val, TreeNode left, TreeNode right) {
          this.val = val;
          this.left = left;
          this.right = right;
      }
  }
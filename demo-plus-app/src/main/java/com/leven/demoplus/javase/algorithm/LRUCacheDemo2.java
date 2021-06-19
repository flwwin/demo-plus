package com.leven.demoplus.javase.algorithm;

import java.util.HashMap;

/**
 * 基于lru算法的缓存设计:最近最少使用
 */
public class LRUCacheDemo2 {
    // 缓存容量
    private int capacity;
    // hashmap用于存数据
    private HashMap<Integer,Node<Integer,Integer>> map;
    // 链表数据结构维护访问次数：1：最近访问的放在头节点 2：缓存容量达到限制的时候，移除链表的尾节
    private DoubleLinkList doubleLinkList;

    public LRUCacheDemo2(int capacity) {
        this.capacity = capacity;
        map = new HashMap();
        doubleLinkList = new DoubleLinkList();
    }

    public int get(int key){
        if (!map.containsKey(key)){
            return -1;
        }
        Node<Integer, Integer> node = map.get(key);
        // 访问的数据node，移动到链表的头head
        doubleLinkList.removeNode(node);
        doubleLinkList.addHead(node);
        return node.value;
    }

    public void  put(Integer key, Integer value){
        if (map.containsKey(key)){
            Node<Integer, Integer> node = map.get(key);
            node.value = value;

            // 新加入数据加入头节点
            doubleLinkList.removeNode(node);
            doubleLinkList.addHead(node);
        }else {
            // 缓存空间满了
            if (map.size() >= capacity){
                // 容量达到限制，淘汰尾节点数据
                Node lastNode = doubleLinkList.getLastNode();
                doubleLinkList.removeNode(lastNode);
                map.remove(lastNode.key);
            }
            Node<Integer, Integer> newNode = new Node<>(key, value);
            map.put(key,newNode);
            doubleLinkList.addHead(newNode);
        }
    }

    class DoubleLinkList<k,v>{
       Node<k,v> head;
       Node<k,v> tail;


        public DoubleLinkList() {
            head = new Node<>();
            tail = new Node<>();
            head.next = tail;
            tail.pre = head;
        }

        // 添加到头
        public void addHead(Node<k,v> node){
            node.next = head.next;
            node.pre = head;

            head.next.pre = node;
            head.next = node;
        }

        public void removeNode(Node<k,v> node){
            node.next.pre = node.pre;
            node.pre.next = node.next;
            node.pre = null;
            node.next = null;
        }

        public Node<k,v> getLastNode(){
            return tail.pre;
        }


    }

    class Node<k,v>{
        k key;
        v value;
        Node<k,v> pre;
        Node<k,v> next;

        public Node() {
            this.pre = this.next = null;
        }

        public Node(k key, v value) {
            this.key = key;
            this.value = value;
        }
    }

  public static void main(String[] args) {
      LRUCacheDemo2 lruCacheDemo = new LRUCacheDemo2(3);
      lruCacheDemo.put(1,1);
      lruCacheDemo.put(2, 2);
      lruCacheDemo.put(3, 3);

      // 1：预期：容量达到限制，淘汰尾节点1，打印：[1, 2, 3]
      System.out.println("lruCacheDemo.keySet() = " + lruCacheDemo.map.keySet());

      // 2：预期：容量达到限制，淘汰尾节点1，打印：[2, 3, 4]
      lruCacheDemo.put(4, 4);
      System.out.println("lruCacheDemo.keySet() = " + lruCacheDemo.map.keySet());

      // 3：预期：访问4的数据，最小访问，加入头节点，打印：[2, 3, 4]
      lruCacheDemo.get(2);
      System.out.println("lruCacheDemo.keySet() = " + lruCacheDemo.map.keySet());

      // 4：预期：加入5，达到限制,淘汰最久访问的缓存3（如果不执行第三步的话淘汰的应该是2），打印：[2, 4, 5]
      lruCacheDemo.put(5, 5);
      System.out.println("lruCacheDemo.keySet() = " + lruCacheDemo.map.keySet());
  }
}

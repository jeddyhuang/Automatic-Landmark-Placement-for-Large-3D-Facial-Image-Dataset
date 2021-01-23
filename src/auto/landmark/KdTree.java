/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auto.landmark;

import java.util.ArrayList;

/**
 *
 * @author rxiao
 */
public class KdTree {
    private Node root = null;
    
    public KdTree(ArrayList<Vertex> list){
        for(Vertex vtx : list){
            this.insert(vtx);
        }
    }
    
    public boolean isEmpty(){
        return root == null;
    }
    
    public int size(){
        return this.nodeSize(root);
    }
    
    private int nodeSize(Node node){
        if(node == null) return 0;
        else return node.size;
    }
    
    public void insert(Vertex pt){
        if(this.isEmpty()) root = this.insertInterval(pt, root, 0);
        else root = this.insertInterval(pt, root, 1);
    }
    
    private Node insertInterval(Vertex pointToInsert, Node node, int level){
        if(node == null){
            Node newNode = new Node(pointToInsert, null, null);
            newNode.size = 1;
            return newNode;
        }
        switch(level % 3){
            case 1:{
                if(pointToInsert.getX() < node.x) node.left = this.insertInterval(pointToInsert, node.left, level + 1);
                else node.right = this.insertInterval(pointToInsert, node.right, level + 1);
                break;
            }
            case 2:{
                if(pointToInsert.getY() < node.y) node.left = this.insertInterval(pointToInsert, node.left, level + 1);
                else node.right = this.insertInterval(pointToInsert, node.right, level + 1);
                break;
            }
            case 0:{
                if(pointToInsert.getZ() < node.z) node.left = this.insertInterval(pointToInsert, node.left, level + 1);
                else node.right = this.insertInterval(pointToInsert, node.right, level + 1);
                break;
            }
        }
        node.size = 1 + this.nodeSize(node.left) + this.nodeSize(node.right);
        return node;
    }
    
    public ArrayList<Vertex> nearest(ArrayList<Vertex> list){
        ArrayList<Vertex> newlist = new ArrayList<Vertex>(list.size());
        for(Vertex vtx : list) newlist.add(this.nearest(vtx));
        return newlist;
    }
    
    public Vertex nearest(Vertex pt){
        if(root == null){
            return null;
        }
        NearestPt closest = new NearestPt(root.pt,Double.MAX_VALUE);
        return nearestInterval(pt, root, closest, 1).closest;
    }
    
    private NearestPt nearestInterval(Vertex targetPoint, Node node, NearestPt closest, int level){
        if (node == null){
            return closest;
        }
        double dist = targetPoint.calcDist(node.pt);
        int newLevel = level + 1;
        if (dist < closest.closestDist){
            closest.closest = node.pt;
            closest.closestDist = dist;
        }
        boolean goLeftOrBottom = false;
        switch(level % 3){
            case 1:{
                if(targetPoint.getX() < node.x) goLeftOrBottom = true;
                break;
            }
            case 2:{
                if(targetPoint.getY() < node.y) goLeftOrBottom = true;
                break;
            }
            case 0:{
                if(targetPoint.getZ() < node.z) goLeftOrBottom = true;
                break;
            }
        }
        if(goLeftOrBottom){
            nearestInterval(targetPoint, node.left, closest, newLevel);
            double bufferDist = createBufferPoint(node.pt, targetPoint, level).calcDist(targetPoint);
            if(bufferDist < closest.closestDist) nearestInterval(targetPoint, node.right, closest, newLevel);
        } else{
            nearestInterval(targetPoint, node.right, closest, newLevel);
            double bufferDist = createBufferPoint(node.pt, targetPoint, level).calcDist(targetPoint);
            if(bufferDist < closest.closestDist) nearestInterval(targetPoint, node.left, closest, newLevel);
        }
        return closest;
    }
    
    private Vertex createBufferPoint(Vertex pt, Vertex targetPoint, int level){
        switch(level % 3){
            case 1: return new Vertex(pt.getX(), targetPoint.getY(), targetPoint.getZ());
            case 2: return new Vertex(targetPoint.getX(), pt.getY(), targetPoint.getZ());
            case 0: return new Vertex(targetPoint.getX(), targetPoint.getY(), pt.getZ());
            default: return new Vertex(0,0,0);
        }
    }
    
    private static class Node {
        public final Vertex pt;
        public Node left, right;
        public int size;
        public double x = 0, y = 0, z = 0;
        
        public Node(Vertex pt, Node left, Node right){
            this.pt = pt;
            this.left = left;
            this.right = right;
            x = pt.getX();
            y = pt.getY();
            z = pt.getZ();
        }
    }
    
    private static class NearestPt {
        public Vertex closest;
        public double closestDist;
        
        public NearestPt(Vertex c, double d){
            closest = c;
            closestDist = d;
        }
    }
}

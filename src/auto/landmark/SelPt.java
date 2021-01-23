/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auto.landmark;

import java.util.ArrayList;

/**
 *
 * @author Jerry
 */
public class SelPt {
    private final String name;
    private int index;
    private Vertex vertex;
    private int checkdist, gridsize, compdist;
    private double[][] targetsheps;
    private final ArrayList<double[][]> imgvals;
    
    public SelPt(Vertex vertex){
        this.vertex = vertex;
        name = vertex.getName();
        index = vertex.getIndex();
        imgvals = new ArrayList<double[][]>();
    }
    
    public void setBounds(int checkdist, int gridsize, int compdist){
        this.checkdist = checkdist;
        this.gridsize = gridsize;
        this.compdist = compdist;
    }
    
    public void addVals(double[][] vals){
        imgvals.add(vals);
    }
    
    public void setVertex(Vertex vertex){
        this.vertex = vertex;
        index = vertex.getIndex();
    }
    
    public void setTrgtVals(double[][] vals){
        targetsheps = vals;
    }
    
    public ArrayList<double[][]> getVals(){
        return imgvals;
    }
    
    public double[][] getTrgtVals(){
        return targetsheps;
    }
    
    public int getIndex(){
        return index;
    }
    
    public String getName(){
        return name;
    }
    
    public Vertex getVertex(){
        return vertex;
    }
    
    public int getCheckdist(){
        return checkdist;
    }
    
    public int getGridsize(){
        return gridsize;
    }
    
    public int getCompdist(){
        return compdist;
    }
}

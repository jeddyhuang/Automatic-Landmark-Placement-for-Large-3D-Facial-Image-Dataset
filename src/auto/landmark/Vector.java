/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auto.landmark;

/**
 *
 * @author Jerry
 */
public class Vector {
    private final int index;
    private final double x, y, z;
    
    public Vector(double x, double y, double z, int index){
        this.x = x;
        this.y = y;
        this.z = z;
        this.index = index;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public double getZ(){
        return z;
    }
    
    public int getIndex(){
        return index;
    }
}

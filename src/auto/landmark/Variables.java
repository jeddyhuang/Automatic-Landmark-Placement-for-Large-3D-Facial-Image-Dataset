/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auto.landmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author rxiao
 */
public class Variables {
    private static final ArrayList<Var> VARIABLES = new ArrayList<Var>();;
    
    public Variables(File[] files) throws Exception{
        VARIABLES.clear();
        for(File lmk : files) VARIABLES.add(new Var(lmk));
    }
    
    public static Var getVar(String name){
        int idx = VARIABLES.indexOf(new Var(name));
        return VARIABLES.get(idx);
    }
    
    public static ArrayList<Var> getVariables(){
        return VARIABLES;
    }
    
    public static void flipOverX(){
        for(Var var : VARIABLES){
            String name = var.getName();
            if(var.getName().charAt(var.getName().length()-1) == 'T') name = var.getName().substring(0, var.getName().length()-1) + "B";
            else if(var.getName().charAt(var.getName().length()-1) == 'B') name = var.getName().substring(0, var.getName().length()-1) + "T";
            double[][] newvals = new double[var.gridsize][var.gridsize];
            for(int y = 0; y < var.gridsize; y ++) for(int x = 0; x < var.gridsize; x ++) newvals[var.gridsize-1-y][x] = var.getVals()[y][x];
            var.resetVals(name, newvals);
        }
    }
    
    public static void flipOverY(){
        for(Var var : VARIABLES){
            String name = var.getName();
            if(var.getName().charAt(var.getName().length()-1) == 'R') name = var.getName().substring(0, var.getName().length()-1) + "L";
            else if(var.getName().charAt(var.getName().length()-1) == 'L') name = var.getName().substring(0, var.getName().length()-1) + "R";
            double[][] newvals = new double[var.gridsize][var.gridsize];
            for(int y = 0; y < var.gridsize; y ++) for(int x = 0; x < var.gridsize; x ++) newvals[y][var.gridsize-1-x] = var.getVals()[y][x];
            var.resetVals(name, newvals);
        }
    }
    
    public static class Var{
        private String name;
        private int checkdist, gridsize, compdist;
        private double[][] compgrid;
        
        public Var(String name){
            this.name = name;
        }
        
        public Var(File input) throws Exception{
            FileReader File = new FileReader(input);
            BufferedReader in = new BufferedReader(File);
            
            String line = in.readLine();
            StringTokenizer linecon = new StringTokenizer(line);
            if(linecon.countTokens() != 8) System.exit(0);
            linecon.nextToken();
            name = linecon.nextToken();
            linecon.nextToken();
            checkdist = Integer.parseInt(linecon.nextToken());
            linecon.nextToken();
            gridsize = Integer.parseInt(linecon.nextToken());
            linecon.nextToken();
            compdist = Integer.parseInt(linecon.nextToken());
            compgrid = new double[gridsize][gridsize];
            for(int i = 0; i < gridsize; i ++){
                String gridline = in.readLine();
                StringTokenizer gridcon = new StringTokenizer(gridline);
                for(int j = 0; j < gridsize; j ++) compgrid[i][j] = Double.parseDouble(gridcon.nextToken());
            }
        }
        
        public void resetVals(String othername, double[][] values){
            name = othername;
            compgrid = values;
        }
        
        public String getName(){
            return name;
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
        
        public double[][] getVals(){
            return compgrid;
        }
        
        @Override
        public boolean equals(Object in){
            Var comp = (Var) in;
            if(name.equalsIgnoreCase(comp.getName())) return true;
            else return false;
        }
    }
}

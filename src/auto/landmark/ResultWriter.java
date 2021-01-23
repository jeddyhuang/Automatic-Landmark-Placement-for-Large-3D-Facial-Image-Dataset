/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auto.landmark;

import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author rxiao
 */
public class ResultWriter {
    public ResultWriter(String directory, ImgCompiler pointset) throws Exception{
        String name = pointset.getName();
        if(!pointset.getSelected().isEmpty()){
            String writerdir = directory + "\\" + name + " Gen Lmks.obj";
            PrintWriter writer = new PrintWriter(writerdir, "UTF-8");
            for(SelPt selpt : pointset.getSelected()){
                writer.println("# " + selpt.getName());
                Vertex vtx = selpt.getVertex();
                writer.println("v " + vtx.getX() + " " + vtx.getY() + " " + vtx.getZ());
            }
            writer.close();
        } else{
            String writerdir = directory + "\\" + name + " No Gen Lmks.txt";
            PrintWriter writer = new PrintWriter(writerdir, "UTF-8");
            writer.close();
        }
    }
    
    public ResultWriter(String directory, PointCompiler pointset) throws Exception{
        String name = pointset.getName();
        if(!pointset.getSelected().isEmpty()){
            String writerdir = directory + "\\" + name + " Gen Lmks.obj";
            PrintWriter writer = new PrintWriter(writerdir, "UTF-8");
            for(SelPt selpt : pointset.getSelected()){
                writer.println("# " + selpt.getName());
                Vertex vtx = selpt.getVertex();
                writer.println("v " + vtx.getX() + " " + vtx.getY() + " " + vtx.getZ());
            }
            writer.close();
        } else{
            String writerdir = directory + "\\" + name + " No Gen Lmks.txt";
            PrintWriter writer = new PrintWriter(writerdir, "UTF-8");
            writer.close();
        }
    }
}
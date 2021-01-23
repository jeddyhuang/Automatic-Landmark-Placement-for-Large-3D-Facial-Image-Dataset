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
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author lenovo
 */
public class ObjReader {
    private final String filename, name, directory;
    //private final ArrayList<String> mtllibs;
    private final ArrayList<Vertex> vertices;
    //private final ArrayList<Texture> textures;
    private final ArrayList<Vector> vectors;
    //private final ArrayList<String> materials;
    private final ArrayList<Facet> facets;
    
    public ObjReader(String directory) throws Exception{
        this.directory = directory;
        filename = new File(directory).getName();
        Pattern base = Pattern.compile("[^_ .]+");
        Matcher mth = base.matcher(filename.substring(0, filename.length()-4));
        if(mth.find()) name = mth.group();
        else name = filename.substring(0, filename.length()-4);
        FileReader File = new FileReader(directory);
        BufferedReader in = new BufferedReader(File);
        
        //mtllibs = new ArrayList<String>();
        vertices = new ArrayList<Vertex>();
        //textures = new ArrayList<Texture>();
        vectors = new ArrayList<Vector>();
        //materials = new ArrayList<String>();
        facets = new ArrayList<Facet>();
        
        int vindex = 1;
        int vtindex = 1;
        int vnindex = 1;
        int findex = 1;
        
        boolean isname = false;
        String ptname = "";
        
        String line = null;
        while((line = in.readLine()) != null){
            StringTokenizer linecon = new StringTokenizer(line);
            switch(linecon.nextToken()){
                case "#":{
                    ptname = linecon.nextToken();
                    isname = true;
                    break;
                }
                /*case "mtllib":{
                    mtllibs.add(linecon.nextToken());
                    break;
                }*/
                case "v":{
                    vertices.add(new Vertex(Double.parseDouble(linecon.nextToken()), Double.parseDouble(linecon.nextToken()), Double.parseDouble(linecon.nextToken()), vindex));
                    if(isname){
                        vertices.get(vindex-1).setName(ptname);
                        isname = false;
                    }
                    vindex ++;
                    break;
                }
                /*case "vt":{
                    textures.add(new Texture(vtindex, Double.parseDouble(linecon.nextToken()), Double.parseDouble(linecon.nextToken())));
                    vtindex ++;
                    break;
                }*/
                case "vn":{
                    vectors.add(new Vector(Double.parseDouble(linecon.nextToken()), Double.parseDouble(linecon.nextToken()), Double.parseDouble(linecon.nextToken()), vnindex));
                    vnindex ++;
                    break;
                }
                /*case "usemtl":{
                    materials.add(linecon.nextToken());
                    facets.add(new ArrayList<Facet>());
                    break;
                }*/
                case "f":{
                    facets.add(new Facet(linecon.nextToken(), linecon.nextToken(), linecon.nextToken(), findex));
                    facets.get(findex-1).setVs(vertices, vectors);
                    findex ++;
                    break;
                }
            }
        }
        in.close();
        File.close();
    }
    
    public void alphabetizeVertices(){
        Collections.sort(vertices);
    }
    
    /*public ArrayList<String> getLibraries(){
        return mtllibs;
    }*/
    
    public ArrayList<Vertex> getVertices(){
        return vertices;
    }
    
    /*public ArrayList<Texture> getTextures(){
        return textures;
    }*/
    
    public ArrayList<Vector> getVectors(){
        return vectors;
    }
    
    /*public ArrayList<String> getMaterials(){
        return materials;
    }*/
    
    public ArrayList<Facet> getFacets(){
        return facets;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDirectory(){
        return directory;
    }
}
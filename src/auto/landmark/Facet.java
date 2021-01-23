package auto.landmark;

import java.util.ArrayList;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * Facet Class
 *
 * This class is responsible for the generation of a Facet Object from an Obj
 * File. A Facet is, in the context of an Obj File, a triangle of 3 Vertex
 * points of which is the basis of the generation of polygon meshes.
 *
 * @author Jerry Wang
 * @version September 13, 2020
 */
public class Facet {
    // Store the Facet index, indexes of the vertices that make up the facet, and the indexes of the vertex normals of the vertices.
    private final int index;
    private final int v1, v2, v3;
    private final int vn1, vn2, vn3;
    // Store the vertex information
    private Vertex vert1, vert2, vert3;
    
    
    /**
     * Generates a Facet object from the relevant Strings pulled from the Obj File
     */
    public Facet(String vc1, String vc2, String vc3, int index){
        this.index = index;
        StringTokenizer vertex1 = new StringTokenizer(vc1, "/");
        StringTokenizer vertex2 = new StringTokenizer(vc2, "/");
        StringTokenizer vertex3 = new StringTokenizer(vc3, "/");
        if(vertex1.countTokens() == 2){
            v1 = Integer.parseInt(vertex1.nextToken());
            vn1 = Integer.parseInt(vertex1.nextToken());
        } else{
            v1 = Integer.parseInt(vertex1.nextToken());
            vertex1.nextToken();
            vn1 = Integer.parseInt(vertex1.nextToken());
        }
        if(vertex2.countTokens() == 2){
            v2 = Integer.parseInt(vertex2.nextToken());
            vn2 = Integer.parseInt(vertex2.nextToken());
        } else{
            v2 = Integer.parseInt(vertex2.nextToken());
            vertex2.nextToken();
            vn2 = Integer.parseInt(vertex2.nextToken());
        }
        if(vertex3.countTokens() == 2){
            v3 = Integer.parseInt(vertex3.nextToken());
            vn3 = Integer.parseInt(vertex3.nextToken());
        } else{
            v3 = Integer.parseInt(vertex3.nextToken());
            vertex3.nextToken();
            vn3 = Integer.parseInt(vertex3.nextToken());
        }
    }
    
    /**
     * Sets the vertex information from the given vertex indexes
     */
    public void setVs(ArrayList<Vertex> vertices, ArrayList<Vector> vectors){
        vertices.get(v1-1).setVector(vectors.get(vn1-1));
        vertices.get(v1-1).addFacetidx(index);
        vert1 = vertices.get(v1-1);
        vertices.get(v2-1).setVector(vectors.get(vn2-1));
        vertices.get(v2-1).addFacetidx(index);
        vert2 = vertices.get(v2-1);
        vertices.get(v3-1).setVector(vectors.get(vn3-1));
        vertices.get(v3-1).addFacetidx(index);
        vert3 = vertices.get(v3-1);
    }
    
    public Vertex getVert1(){
        return vert1;
    }
    
    public Vertex getVert2(){
        return vert2;
    }
    
    public Vertex getVert3(){
        return vert3;
    }
    
    public int getVindex1(){
        return v1;
    }
    
    public int getVindex2(){
        return v2;
    }
    
    public int getVindex3(){
        return v3;
    }
    
    public int getIndex(){
        return index;
    }
    
    /**
     * Checks if the Facet contains the vertex
     */
    public boolean contains(Vertex v){
        if(Objects.equals(vert1, v)) return true;
        if(Objects.equals(vert2, v)) return true;
        if(Objects.equals(vert3, v)) return true;
        return false;
    }
    
    /**
     * Returns the other 2 vertices in an array if the Facet contains the vertex
     */
    public Vertex[] neighboring(Vertex v){
        Vertex[] temp = new Vertex[2];
        if(Objects.equals(vert1, v)){
            temp[0] = vert2;
            temp[1] = vert3;
            return temp;
        } else if(Objects.equals(vert2, v)){
            temp[0] = vert1;
            temp[1] = vert3;
            return temp;
        } else if(Objects.equals(vert3, v)){
            temp[0] = vert1;
            temp[1] = vert2;
            return temp;
        }
        return null;
    }
}
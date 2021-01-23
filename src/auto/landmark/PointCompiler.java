/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auto.landmark;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author lenovo
 */
public class PointCompiler {
    private final String name;
    private final ArrayList<SelPt> setpoints;
    private final ArrayList<Vertex> vertices;
    private final ArrayList<Facet> facets;
    private ArrayList<ArrayList<Vertex>> allneighbors;
    private ArrayList<ArrayList<ArrayList<Vertex>>> neighbors;
    
    public PointCompiler(Variables vars, ObjReader objpoints, ObjReader objpatterns){
        name = objpoints.getName();
        vertices = objpoints.getVertices();
        facets = objpoints.getFacets();
        setpoints = new ArrayList<SelPt>(objpatterns.getVertices().size());
        for(Vertex point : objpatterns.getVertices()){
            SelPt curr = new SelPt(point);
            Variables.Var var = vars.getVar(curr.getName());
            curr.setTrgtVals(var.getVals());
            curr.setBounds(var.getCheckdist(), var.getGridsize(), var.getCompdist());
            curr.setVertex(point.closestVtx(vertices));
            setpoints.add(curr);
        }
    }
    
    public PointCompiler(Variables vars, ObjReader objpoints, ObjReader temppoints, ObjReader temppatterns){
        name = objpoints.getName();
        vertices = objpoints.getVertices();
        facets = objpoints.getFacets();
        setpoints = new ArrayList<SelPt>(temppatterns.getVertices().size());
        for(Vertex point : temppatterns.getVertices()){
            SelPt curr = new SelPt(point);
            Variables.Var var = vars.getVar(curr.getName());
            curr.setTrgtVals(var.getVals());
            curr.setBounds(var.getCheckdist(), var.getGridsize(), var.getCompdist());
            curr.setVertex(point.closestVtx(temppoints.getVertices()).closestNormVtx(vertices));
            setpoints.add(curr);
        }
    }
    
    public PointCompiler(Variables vars, ICPRunner icprun){
        ObjReader objpoints = icprun.getOtherReader();
        name = objpoints.getName();
        vertices = objpoints.getVertices();
        facets = objpoints.getFacets();
        setpoints = new ArrayList<SelPt>(icprun.getFinalPoints().size());
        for(Vertex point : icprun.getFinalPoints()){
            SelPt curr = new SelPt(point);
            Variables.Var var = vars.getVar(curr.getName());
            curr.setTrgtVals(var.getVals());
            curr.setBounds(var.getCheckdist(), var.getGridsize(), var.getCompdist());
            setpoints.add(curr);
        }
    }
    
    /*public PointCompiler(ObjReader objpoints, ObjReader objpatterns){
        name = objpoints.getName();
        vertices = objpoints.getVertices();
        facets = objpoints.getFacets();
        setpoints = new ArrayList<SelPt>(objpatterns.getVertices().size());
        for(Vertex point : objpatterns.getVertices()){
            SelPt curr = new SelPt(point);
            SetVariables var = new SetVariables(curr.getName());
            curr.setBounds(var.checkdist, var.gridsize, var.compdist);
            curr.setVertex(point.closestVtx(vertices));
            setpoints.add(curr);
        }
    }*/
    
    public void setallNeighbors(){
        allneighbors = new ArrayList<ArrayList<Vertex>>(vertices.size());
        for(int vidx = 0; vidx < vertices.size(); vidx ++){
            allneighbors.add(new ArrayList<Vertex>());
            allneighbors.get(vidx).add(vertices.get(vidx));
            HashSet<Vertex> values = new HashSet<Vertex>();
            for(int fidx : vertices.get(vidx).getFacetidxs()){
                if(facets.get(fidx-1).contains(vertices.get(vidx))){
                    Vertex[] hold = facets.get(fidx-1).neighboring(vertices.get(vidx));
                    if(!values.contains(hold[0])){
                        allneighbors.get(vidx).add(hold[0]);
                        values.add(hold[0]);
                    }
                    if(!values.contains(hold[1])){
                        allneighbors.get(vidx).add(hold[1]);
                        values.add(hold[1]);
                    }
                }
            }
        }
    }
    
    public void setRegions(){
        neighbors = new ArrayList<ArrayList<ArrayList<Vertex>>>(setpoints.size());
        for(int ptidx = 0; ptidx < setpoints.size(); ptidx ++){
            neighbors.add(new ArrayList<ArrayList<Vertex>>());
            neighbors.get(ptidx).add(new ArrayList<Vertex>());
            neighbors.get(ptidx).get(0).add(setpoints.get(ptidx).getVertex());
            if(setpoints.get(ptidx).getCompdist() > 0){
                for(int nidx = 1; nidx < allneighbors.get(setpoints.get(ptidx).getIndex()-1).size(); nidx ++){
                    neighbors.get(ptidx).add(new ArrayList<Vertex>());
                    neighbors.get(ptidx).get(nidx).add(allneighbors.get(setpoints.get(ptidx).getIndex()-1).get(nidx));
                }
            }
            if(setpoints.get(ptidx).getCompdist() > 1){
                HashSet<Vertex> values = new HashSet<Vertex>(allneighbors.get(setpoints.get(ptidx).getIndex()-1));
                for(int crg = 1; crg < setpoints.get(ptidx).getCompdist(); crg ++){
                    int size = neighbors.get(ptidx).size();
                    for(int nbr = 1; nbr < size; nbr ++){
                        for(int nidx = 1; nidx < allneighbors.get(neighbors.get(ptidx).get(nbr).get(0).getIndex()-1).size(); nidx ++){
                            if(!values.contains(allneighbors.get(neighbors.get(ptidx).get(nbr).get(0).getIndex()-1).get(nidx))){
                                neighbors.get(ptidx).add(new ArrayList<Vertex>());
                                neighbors.get(ptidx).get(neighbors.get(ptidx).size()-1).add(allneighbors.get(neighbors.get(ptidx).get(nbr).get(0).getIndex()-1).get(nidx));
                                values.add(allneighbors.get(neighbors.get(ptidx).get(nbr).get(0).getIndex()-1).get(nidx));
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void setNeighbors(){
        for(int ptidx = 0; ptidx < setpoints.size(); ptidx ++){
            if(setpoints.get(ptidx).getCheckdist() > 0){
                for(int cidx = 0; cidx < neighbors.get(ptidx).size(); cidx ++){
                    for(int nidx = 1; nidx < allneighbors.get(neighbors.get(ptidx).get(cidx).get(0).getIndex()-1).size(); nidx ++){
                        neighbors.get(ptidx).get(cidx).add(allneighbors.get(neighbors.get(ptidx).get(cidx).get(0).getIndex()-1).get(nidx));
                    }
                }
            }
            if(setpoints.get(ptidx).getCheckdist() > 1){
                for(int cidx = 0; cidx < neighbors.get(ptidx).size(); cidx ++){
                    HashSet<Vertex> values = new HashSet<Vertex>(neighbors.get(ptidx).get(cidx));
                    for(int nrg = 1; nrg < setpoints.get(ptidx).getCompdist(); nrg ++){
                        int size = neighbors.get(ptidx).get(cidx).size();
                        for(int nbr = 1; nbr < size; nbr ++){
                            for(int nidx = 1; nidx < allneighbors.get(neighbors.get(ptidx).get(cidx).get(nbr).getIndex()-1).size(); nidx ++){
                                if(!values.contains(allneighbors.get(neighbors.get(ptidx).get(cidx).get(nbr).getIndex()-1).get(nidx))){
                                    neighbors.get(ptidx).get(cidx).add(allneighbors.get(neighbors.get(ptidx).get(cidx).get(nbr).getIndex()-1).get(nidx));
                                    values.add(allneighbors.get(neighbors.get(ptidx).get(cidx).get(nbr).getIndex()-1).get(nidx));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public ArrayList<SelPt> getSelected(){
        return setpoints;
    }
    
    public ArrayList<Vertex> getVertices(){
        return vertices;
    }
    
    public ArrayList<Facet> getFacets(){
        return facets;
    }
    
    public ArrayList<ArrayList<ArrayList<Vertex>>> getNeighbors(){
        return neighbors;
    }
    
    public String getName(){
        return name;
    }
}
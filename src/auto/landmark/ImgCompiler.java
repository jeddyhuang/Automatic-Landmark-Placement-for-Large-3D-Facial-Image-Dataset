/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auto.landmark;

import java.util.ArrayList;

/**
 *
 * @author lenovo
 */
public class ImgCompiler {
    private final String name;
    private final ArrayList<ArrayList<ArrayList<Vertex>>> neighborhood;
    private final ArrayList<SelPt> setpoints;
    private final ArrayList<ArrayList<ArrayList<Double>>> displacements;
    
    public ImgCompiler(PointCompiler ptcom){
        name = ptcom.getName();
        neighborhood = ptcom.getNeighbors();
        setpoints = ptcom.getSelected();
        displacements = new ArrayList<ArrayList<ArrayList<Double>>>(neighborhood.size());
        for(int ptidx = 0; ptidx < neighborhood.size(); ptidx ++){
            displacements.add(new ArrayList<ArrayList<Double>>(neighborhood.get(ptidx).size()));
            for(int nidx = 0; nidx < neighborhood.get(ptidx).size(); nidx ++){
                displacements.get(ptidx).add(new ArrayList<Double>(neighborhood.get(ptidx).get(nidx).size()));
                double ovnx = neighborhood.get(ptidx).get(nidx).get(0).getVector().getX();
                double ovny = neighborhood.get(ptidx).get(nidx).get(0).getVector().getY();
                double ovnz = neighborhood.get(ptidx).get(nidx).get(0).getVector().getZ();
                
                double ovx = neighborhood.get(ptidx).get(nidx).get(0).getX();
                double ovy = neighborhood.get(ptidx).get(nidx).get(0).getY();
                double ovz = neighborhood.get(ptidx).get(nidx).get(0).getZ();
                
                for(Vertex vtx : neighborhood.get(ptidx).get(nidx)){
                    double nvx = vtx.getX(), nvy = vtx.getY(), nvz = vtx.getZ();
                    double dvx = nvx - ovx, dvy = nvy - ovy, dvz = nvz - ovz;
                    double dp = ((ovnx * dvx) + (ovny * dvy) + (ovnz * dvz));
                    displacements.get(ptidx).get(nidx).add(dp);
                }
            }
        }
    }
    
    public void runShepardsMethod(double imglimit){
        int weightxp = 2;
        for(int ptidx = 0; ptidx < neighborhood.size(); ptidx ++){
            int imgsize = setpoints.get(ptidx).getGridsize();
            int mid = (int)(imgsize / 2);
            for(int nidx = 0; nidx < neighborhood.get(ptidx).size(); nidx ++){
                double[][] val = new double[imgsize][imgsize];
                Vertex vy = this.yvertex(neighborhood.get(ptidx).get(nidx).get(0).getVector());
                Vertex vx = this.xvertex(vy, neighborhood.get(ptidx).get(nidx).get(0).getVector());
                for(int y = 0; y < imgsize; y ++) for(int x = 0; x < imgsize; x ++){
                    double my = imglimit * (mid - y);
                    double mx = imglimit * (x - mid);
                    
                    double xcoord = neighborhood.get(ptidx).get(nidx).get(0).getX() + (my * vy.getX()) + (mx * vx.getX());
                    double ycoord = neighborhood.get(ptidx).get(nidx).get(0).getY() + (my * vy.getY()) + (mx * vx.getY());
                    double zcoord = neighborhood.get(ptidx).get(nidx).get(0).getZ() + (my * vy.getZ()) + (mx * vx.getZ());
                    
                    double tdis = 0;
                    double tweight = 0;
                    double[] ddis = new double[displacements.get(ptidx).get(nidx).size()];
                    double[] dweight = new double[displacements.get(ptidx).get(nidx).size()];
                    double finalv = 0;
                    
                    for(int nbr = 0; nbr < displacements.get(ptidx).get(nidx).size(); nbr ++){
                        double px = xcoord - neighborhood.get(ptidx).get(nidx).get(nbr).getX();
                        double py = ycoord - neighborhood.get(ptidx).get(nidx).get(nbr).getY();
                        double pz = zcoord - neighborhood.get(ptidx).get(nidx).get(nbr).getZ();
                        
                        double dis = Math.sqrt(Math.pow(py, 2) + Math.pow(px, 2) + Math.pow(pz, 2));
                        ddis[nbr] = dis;
                        tdis += Math.pow(dis, -weightxp);
                    }
                    
                    for(int nbr = 0; nbr < displacements.get(ptidx).get(nidx).size(); nbr ++){
                        double sdis = Math.pow(ddis[nbr], -weightxp);
                        dweight[nbr] = (sdis / tdis);
                        tweight += (sdis / tdis);
                    }
                    
                    for(int nbr = 0; nbr < displacements.get(ptidx).get(nidx).size(); nbr ++){
                        finalv += ((dweight[nbr] / tweight) * (displacements.get(ptidx).get(nidx).get(nbr)));
                    }
                    if(Double.isNaN(finalv)) val[y][x] = 0.0;
                    else val[y][x] = finalv;
                }
                setpoints.get(ptidx).addVals(val);
            }
        }
    }
    
    public void compShepardsMethod(){
        for(int ptidx = 0; ptidx < neighborhood.size(); ptidx ++){
            Vertex closest = null;
            double trgtval = 0;
            for(double[] row : setpoints.get(ptidx).getTrgtVals()) for(double val : row) trgtval += val;
            double dist = Double.POSITIVE_INFINITY;
            for(int nidx = 0; nidx < neighborhood.get(ptidx).size(); nidx ++){
                double compval = 0;
                for(double[] row : setpoints.get(ptidx).getVals().get(nidx)) for(double val : row) compval += val;
                double newdist = Math.abs(trgtval - compval);
                if(newdist < dist){
                    closest = neighborhood.get(ptidx).get(nidx).get(0);
                    dist = newdist;
                }
            }
            setpoints.get(ptidx).setVertex(closest);
        }
    }
    
    public void compShepardsMethod(double threshhold){
        boolean latch1 = true;
        for(int ptidx = 0; ptidx < neighborhood.size() && latch1; ptidx ++){
            Vertex closest = null;
            double tgtval = Double.POSITIVE_INFINITY;
            int len = setpoints.get(ptidx).getTrgtVals().length;
            for(int nidx = 0; nidx < neighborhood.get(ptidx).size(); nidx ++){
                boolean latch2 = true;
                double compval = 0;
                for(int y = 0; y < len && latch2; y ++) for(int x = 0; x < len && latch2; x++){
                    double val = Math.abs(setpoints.get(ptidx).getTrgtVals()[y][x] - setpoints.get(ptidx).getVals().get(nidx)[y][x]);
                    if(val > threshhold) latch2 = false;
                    else compval += val;
                }
                if(latch2 && compval < tgtval){
                    closest = neighborhood.get(ptidx).get(nidx).get(0);
                    tgtval = compval;
                }
            }
            if(closest == null){
                latch1 = false;
                setpoints.clear();
            } else setpoints.get(ptidx).setVertex(closest);
        }
    }
    
    public Vertex yvertex(Vector normal){
        double x = 0, y = 1, z = 0;
        double dp = normal.getY();
        
        double nx = dp * normal.getX(), ny = dp * normal.getY(), nz = dp * normal.getZ();
        double fx = x - nx, fy = y - ny, fz = z - nz;
        
        double dis = Math.sqrt(Math.pow(fx, 2) + Math.pow(fy, 2) + Math.pow(fz, 2));
        fx /= dis;
        fy /= dis;
        fz /= dis;
        
        return new Vertex(fx, fy, fz);
    }
    
    public Vertex xvertex(Vertex basevector, Vector crossvector){
        double x = (basevector.getY() * crossvector.getZ()) - (basevector.getZ() * crossvector.getY());
        double y = (basevector.getZ() * crossvector.getX()) - (basevector.getX() * crossvector.getZ());
        double z = (basevector.getX() * crossvector.getY()) - (basevector.getY() * crossvector.getX());
        
        double dis = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        x /= dis;
        y /= dis;
        z /= dis;
        
        return new Vertex(x, y, z);
    }
    
    public ArrayList<SelPt> getSelected(){
        return setpoints;
    }
    
    public String getName(){
        return name;
    }
}

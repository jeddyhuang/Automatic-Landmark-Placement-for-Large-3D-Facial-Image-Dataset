package auto.landmark;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import java.util.ArrayList;

/**
 * ICPRunner Class
 *
 * This class is responsible for the iteration control of ICP as well as ICP
 * functionality itself.
 *
 * @author Jerry Wang
 * @version September 13, 2020
 */
public class ICPRunner {
    private final ObjReader othPoints, tgtPoints;
    private ArrayList<Vertex> tgtpts, seltgtpts;
    private ArrayList<Vertex> othpts, selothpts;
    private ArrayList<ArrayList<Integer>> checkidx;
    private ArrayList<Vertex> finpts;
    
    public ICPRunner(ObjReader othPoints, ObjReader tgtPoints, ObjReader seltgtPoints){
        this.othPoints = othPoints;
        this.tgtPoints = tgtPoints;
        selothpts = new ArrayList<Vertex>(seltgtPoints.getVertices().size());
        seltgtpts = new ArrayList<Vertex>(seltgtPoints.getVertices().size());
        for(Vertex vtx : seltgtPoints.getVertices()){
            selothpts.add(vtx.closestVtx(othPoints.getVertices()));
            seltgtpts.add(vtx.closestVtx(tgtPoints.getVertices()));
        }
        othpts = this.cropObj(othPoints.getVertices(), selothpts);
        tgtpts = this.cropObj(tgtPoints.getVertices(), seltgtpts);
    }
    
    public void runComparisons(int iterations, int checklimit){
        if(othpts.size() < tgtpts.size()) runComparisons1(iterations, checklimit);
        else runComparisons2(iterations, checklimit);
    }
    
    private void runComparisons1(int iterations, int checklimit){
        boolean latch1 = true;
        checkidx = new ArrayList<ArrayList<Integer>>(checklimit);
        if(iterations < 1) latch1 = false;
        for(int times = 0; times < iterations && latch1; times ++){
            IterativeClosestPoint runner;
            if(times == 0) runner = new IterativeClosestPoint(othpts, tgtpts);
            else if(times <= 50) runner = new IterativeClosestPoint(othpts, tgtpts, false);
            else runner = new IterativeClosestPoint(othpts, tgtpts, true);
            othpts = runner.getNewPoints();
            ArrayList<Integer> curridx = new ArrayList<Integer>(seltgtpts.size());
            selothpts.clear();
            for(Vertex vtx : seltgtpts){
                Vertex selpt = vtx.closestNormVtx(othpts);
                //Vertex selpt = vtx.closestVtx(tgtpts);
                selothpts.add(selpt);
                curridx.add(selpt.getIndex());
            }
            if(checkidx.size() < checklimit) checkidx.add(curridx);
            else{
                boolean latch2 = true;
                for(int i = checklimit-1; i > -1; i --){
                    if(!checkidx.get(i).equals(curridx)){
                        for(; i > -1; i --) checkidx.remove(i);
                        latch2 = false;
                    }
                }
                if(latch2) latch1 = false;
                else checkidx.add(curridx);
            }
        }
        finpts = new ArrayList<Vertex>(seltgtpts.size());
        for(Vertex vtx : selothpts){
            Vertex newvtx = othPoints.getVertices().get(vtx.getIndex()-1);
            newvtx.setName(vtx.getName());
            finpts.add(newvtx);
        }
    }
    
    private void runComparisons2(int iterations, int checklimit){
        boolean latch1 = true;
        checkidx = new ArrayList<ArrayList<Integer>>(checklimit);
        if(iterations < 1) latch1 = false;
        for(int times = 0; times < iterations && latch1; times ++){
            IterativeClosestPoint runner;
            if(times == 0) runner = new IterativeClosestPoint(tgtpts, othpts);
            else if(times <= 50) runner = new IterativeClosestPoint(tgtpts, othpts, false);
            else runner = new IterativeClosestPoint(tgtpts, othpts, true);
            tgtpts = runner.getNewPoints();
            for(int i = 0; i < seltgtpts.size(); i ++){
                String name = seltgtpts.get(i).getName();
                int index = seltgtpts.get(i).getIndex();
                for(Vertex vtx : tgtpts) if(vtx.getIndex() == index){
                    vtx.setName(name);
                    seltgtpts.set(i, vtx);
                }
            }
            ArrayList<Integer> curridx = new ArrayList<Integer>(seltgtpts.size());
            selothpts.clear();
            for(Vertex vtx : seltgtpts){
                Vertex selpt = vtx.closestVtx(othpts);
                selothpts.add(selpt);
                curridx.add(selpt.getIndex());
            }
            if(checkidx.size() < checklimit) checkidx.add(curridx);
            else{
                boolean latch2 = true;
                for(int i = checklimit-1; i > -1; i --){
                    if(!checkidx.get(i).equals(curridx)){
                        for(; i > -1; i --) checkidx.remove(i);
                        latch2 = false;
                    }
                }
                if(latch2) latch1 = false;
                else checkidx.add(curridx);
            }
        }
        finpts = new ArrayList<Vertex>(selothpts.size());
        for(Vertex vtx : selothpts){
            Vertex newvtx = othPoints.getVertices().get(vtx.getIndex()-1);
            newvtx.setName(vtx.getName());
            finpts.add(newvtx);
        }
    }
    
    public ArrayList<Vertex> cropObj(ArrayList<Vertex> target, ArrayList<Vertex> selected){
        double[] far = new double[]{0,0}, sum = new double[]{0,0}, limit = new double[]{0,0};
        for(Vertex vtx : selected){
            if(vtx.getY() < far[0]) far[0] = vtx.getY();
            if(vtx.getZ() < far[1]) far[1] = vtx.getZ();
            sum[0] += vtx.getY();
            sum[1] += vtx.getZ();
        }
        sum[0] /= selected.size();
        sum[1] /= selected.size();
        limit[0] = sum[0] - (sum[0] - far[0]) * 1.5;
        limit[1] = sum[1] - (sum[1] - far[1]) * 1.25;
        ArrayList<Vertex> cropped = new ArrayList<Vertex>(target);
        for(int i = 0; i < cropped.size(); i ++){
            if(cropped.get(i).getY() < limit[0]){
                cropped.remove(i);
                i--;
            } else if(cropped.get(i).getZ() < limit[1]){
                cropped.remove(i);
                i--;
            }
        }
        return cropped;
    }
    
    public ArrayList<Vertex> getFinalPoints(){
        return finpts;
    }
    
    public ObjReader getTargetReader(){
        return tgtPoints; 
    }
    
    public ObjReader getOtherReader(){
        return othPoints;
    }
    
    private static class IterativeClosestPoint {
        private ArrayList<Vertex> original, target, applied;
        private KdTree pointtree;
        
        public IterativeClosestPoint(ArrayList<Vertex> newPoints, ArrayList<Vertex> tgtPoints, boolean scaling){
            original = newPoints;
            pointtree = new KdTree(original);
            target = pointtree.nearest(tgtPoints);
            Vertex omed = getExpectedValue(original), tmed = getExpectedValue(target);
            double[][] covarianceMatrix = getCovarianceMatrix(original, target, omed, tmed);
            double[][] quaternion = createQuaternion(covarianceMatrix);
            double[] eigenVector = getMaxEigenVector(quaternion);
            double[][] rotationMatrix = getRotationMatrix(eigenVector);
            double scalingFactor;
            if(scaling) scalingFactor = getScaling(tmed, rotationMatrix, omed);
            else scalingFactor = 1;
            double[] translationalVector = getTranslationVector(tmed, rotationMatrix, scalingFactor, omed);
            this.applymatrix(rotationMatrix, scalingFactor, translationalVector);
        }
        
        public IterativeClosestPoint(ArrayList<Vertex> newPoints, ArrayList<Vertex> tgtPoints){
            original = newPoints;
            target = tgtPoints;
            this.translate();
        }
        
        private Vertex getExpectedValue(ArrayList<Vertex> u){
            double x = 0, y = 0, z = 0;

            for(int i =0; i < u.size(); i++){
                x += u.get(i).getX();
                y += u.get(i).getY();
                z += u.get(i).getZ();
            }
            x /= u.size();
            y /= u.size();
            z /= u.size();
            return new Vertex(x,y,z);
        }
        
        private double[][] getCovarianceMatrix(ArrayList<Vertex> p, ArrayList<Vertex> x, Vertex omed, Vertex tmed){       
            double[][] cov = new double[3][3];
            for(int i = 0; i < p.size(); i++){
                cov[0][0] += p.get(i).getX()*x.get(i).getX();
                cov[0][1] += p.get(i).getX()*x.get(i).getY();
                cov[0][2] += p.get(i).getX()*x.get(i).getZ();

                cov[1][0] += p.get(i).getY()*x.get(i).getX();
                cov[1][1] += p.get(i).getY()*x.get(i).getY();
                cov[1][2] += p.get(i).getY()*x.get(i).getZ();

                cov[2][0] += p.get(i).getZ()*x.get(i).getX();
                cov[2][1] += p.get(i).getZ()*x.get(i).getY();
                cov[2][2] += p.get(i).getZ()*x.get(i).getZ();
            }
            for(int i = 0; i < cov.length; i ++) for(int j = 0; j < cov[0].length; j ++){
                cov[i][j] /= p.size();
            }
            
            cov[0][0] -= omed.getX()*tmed.getX();
            cov[0][1] -= omed.getX()*tmed.getY();
            cov[0][2] -= omed.getX()*tmed.getZ();

            cov[1][0] -= omed.getY()*tmed.getX();
            cov[1][1] -= omed.getY()*tmed.getY();
            cov[1][2] -= omed.getY()*tmed.getZ();

            cov[2][0] -= omed.getZ()*tmed.getX();
            cov[2][1] -= omed.getZ()*tmed.getY();
            cov[2][2] -= omed.getZ()*tmed.getZ();
            return cov;
        }
        
        private double getTrace(double[][] e){
            double trace = 0;
            for(int i = 0; i < e.length; i++){
                trace+=e[i][i];
            }
            return trace;
        }
        
        private double[][] createQuaternion(double[][] e){
            double[][] q = new double[4][4];
            
            q[0][0] = getTrace(e);
            
            q[1][0] = e[1][2] - e[2][1];
            q[2][0] = e[2][0] - e[0][2];
            q[3][0] = e[0][1] - e[1][0];
            
            q[0][1] = q[1][0];
            q[0][2] = q[2][0];
            q[0][3] = q[3][0];
            
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    q[1+i][1+j] = e[i][j] + e[j][i] - (i==j?q[0][0]:0);
                }
            }
            return q;
        }

        private double[] getMaxEigenVector(double[][] q){        
            Matrix m = new Matrix(q);
            EigenvalueDecomposition evd = new EigenvalueDecomposition(m);
            double[] eigenValues = evd.getRealEigenvalues();
            double max = Double.NEGATIVE_INFINITY;
            int index = 0;

            for(int i =0; i < eigenValues.length; i++){
                if(eigenValues[i] > max){
                    max = eigenValues[i];
                    index = i;
                }
            }
            return evd.getV().transpose().getArray()[index];
        }
        
        private double[][] getRotationMatrix(double[] rotationVector){
            double[][] r = new double[3][3];
            double[] rv = rotationVector;
            
            r[0][0] = rv[0]*rv[0] + rv[1]*rv[1] - rv[2]*rv[2] - rv[3]*rv[3];
            r[1][1] = rv[0]*rv[0] + rv[2]*rv[2] - rv[1]*rv[1] - rv[3]*rv[3];
            r[2][2] = rv[0]*rv[0] + rv[3]*rv[3] - rv[1]*rv[1] - rv[2]*rv[2];
            
            r[0][1] = 2 * (rv[1]*rv[2] - rv[0]*rv[3]);
            r[0][2] = 2 * (rv[1]*rv[3] + rv[0]*rv[2]);
            
            r[1][0] = 2 * (rv[1]*rv[2] + rv[0]*rv[3]);
            r[1][2] = 2 * (rv[2]*rv[3] - rv[0]*rv[1]);
            
            r[2][0] = 2 * (rv[1]*rv[3] - rv[0]*rv[2]);
            r[2][1] = 2 * (rv[2]*rv[3] + rv[0]*rv[1]);
            
            return r;
        }
        
        private double getScaling(Vertex tmed, double[][] r, Vertex omed){
            double[] prod1a = new double[3];
            double prod1b, prod2;
            prod1a[0] = omed.getX() * r[0][0] + omed.getY() * r[0][1] + omed.getZ() * r[0][2];
            prod1a[1] = omed.getX() * r[1][0] + omed.getY() * r[1][1] + omed.getZ() * r[1][2];
            prod1a[2] = omed.getX() * r[2][0] + omed.getY() * r[2][1] + omed.getZ() * r[2][2];
            
            prod1b = prod1a[0] * tmed.getX() + prod1a[1] * tmed.getY() + prod1a[2] * tmed.getZ();
            prod2 = omed.getX() * omed.getX() + omed.getY() * omed.getY() + omed.getZ() * omed.getZ();
            return prod1b / prod2;
        }
        
        private double[] getTranslationVector(Vertex tmed, double[][] r, double scaling, Vertex omed){
            double[] t = new double[3];
            t[0] = tmed.getX() - scaling * (r[0][0]*omed.getX() + r[0][1]*omed.getY() + r[0][2]*omed.getZ());
            t[1] = tmed.getY() - scaling * (r[1][0]*omed.getX() + r[1][1]*omed.getY() + r[1][2]*omed.getZ());
            t[2] = tmed.getZ() - scaling * (r[2][0]*omed.getX() + r[2][1]*omed.getY() + r[2][2]*omed.getZ());
            return t;
        }
        
        private void translate(){
            ArrayList<Vertex> newlist = new ArrayList<Vertex>(original.size());
            Vertex omed = this.getExpectedValue(original);
            Vertex tmed = this.getExpectedValue(target);
            double[] translate = new double[]{tmed.getX()-omed.getX(),tmed.getY()-omed.getY(),tmed.getZ()-omed.getZ()};
            for(Vertex vtx : original){
                newlist.add(new Vertex(vtx.getX()+translate[0],vtx.getY()+translate[1],vtx.getZ()+translate[2],vtx.getIndex()));
            }
            applied = newlist;
        }
        
        private void applymatrix(double[][] rotation, double scaling, double[] translation){
            ArrayList<Vertex> newlist = new ArrayList<Vertex>();
            for(int i = 0; i < original.size(); i ++){
                double[] newpoint = new double[3];
                newpoint[0] = original.get(i).getX() * rotation[0][0] + original.get(i).getY() * rotation[0][1] + original.get(i).getZ() * rotation[0][2];
                newpoint[1] = original.get(i).getX() * rotation[1][0] + original.get(i).getY() * rotation[1][1] + original.get(i).getZ() * rotation[1][2];
                newpoint[2] = original.get(i).getX() * rotation[2][0] + original.get(i).getY() * rotation[2][1] + original.get(i).getZ() * rotation[2][2];
                
                newpoint[0] *= scaling;
                newpoint[1] *= scaling;
                newpoint[2] *= scaling;
                
                newpoint[0] += translation[0];
                newpoint[1] += translation[1];
                newpoint[2] += translation[2];
                newlist.add(new Vertex(newpoint[0], newpoint[1], newpoint[2], original.get(i).getIndex()));
            }
            applied = newlist;
        }
        
        public ArrayList<Vertex> getNewPoints(){
            return applied;
        }
        
        public ArrayList<Vertex> getOriginalPoints(){
            return original;
        }
        
        public ArrayList<Vertex> getTargetPoints(){
            return target;
        }
    }
}

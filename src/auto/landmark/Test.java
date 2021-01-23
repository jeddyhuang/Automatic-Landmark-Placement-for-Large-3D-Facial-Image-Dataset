package auto.landmark;

import java.io.File;

/**
 * Testing Class
 *
 * This class is a testing workplace built explicitly for the testing the code
 * within this package in order to gauge it's effectiveness as well as it's
 * usability. Do NOT take this class as an example of what you should do.
 * Please refer to the main AutoLandmark class to follow the main runner
 * code functionality.
 *
 * @author Jerry Wang
 * @version September 13, 2020
 */
public class Test {
    /**
     * Main Runner Code
     */
    public static void main(String[] args) throws Exception {
        ObjReader temppoints = new ObjReader("C:\\Users\\rxiao\\Desktop\\Obj Files\\460042.obj");
        ObjReader temppatterns = new ObjReader("C:\\Users\\rxiao\\Desktop\\39 Meshlab Picked Files\\460042 Landmarks.obj");
        temppatterns.alphabetizeVertices();
        File[] files = new File[]{new File("C:\\Users\\rxiao\\Desktop\\Test\\ACPL.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\ACPR.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\DACL.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\DACR.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\ECTL.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\ECTR.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\G.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\GN.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\GOL.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\GOR.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\LI.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\LS.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\M.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\MIOL.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\MIOR.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\MLS.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\MSOL.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\MSOR.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\N.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\PG.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\SN.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\ZL.txt"),
                                  new File("C:\\Users\\rxiao\\Desktop\\Test\\ZR.txt")};
        Variables vars = new Variables(files);
        
        ObjReader objpoints = new ObjReader("C:\\Users\\rxiao\\Desktop\\Obj Files\\123592.obj");
        ICPRunner icp = new ICPRunner(objpoints, temppoints, temppatterns);
        icp.runComparisons(1, 4);
        PointCompiler ptcomp = new PointCompiler(vars, icp);
        ptcomp.setallNeighbors();
        ptcomp.setRegions();
        ptcomp.setNeighbors();
        ImgCompiler imgcomp = new ImgCompiler(ptcomp);
        imgcomp.runShepardsMethod(0.75);
        imgcomp.compShepardsMethod();
        ResultWriter writer = new ResultWriter("C:\\Users\\rxiao\\Desktop\\Test", imgcomp);
    }
}

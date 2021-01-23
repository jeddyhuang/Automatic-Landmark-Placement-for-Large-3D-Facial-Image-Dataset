package auto.landmark;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

/**
 * AutoLandmark Class
 *
 * This class is the main runner class for the developed code of the automatic
 * landmarking program. When run, this class generates various pop-up windows
 * that direct what files are to be input. After acquiring the paths for said
 * files, the program will then attempt to automatically landmark those files
 * and save those landmarks at the specified folder by splitting the tasks
 * to the correct number of threads.
 *
 * @author Jerry Wang
 * @version September 13, 2020
 */
public class AutoLandmark {
    //Number of threads allocated that the program can utilize
    private static final int THREADS = 96;
    
    /**
     * Main Runner Code
     */
    public static void main(String[] args) throws Exception {
        JFileChooser tempobj = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        tempobj.setDialogTitle("Select the Template .obj File to Base Comparisons on:");
        tempobj.setAcceptAllFileFilterUsed(false);
        tempobj.addChoosableFileFilter(new FileNameExtensionFilter(".obj Files", "obj"));
        if(tempobj.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) System.exit(0);
        
        JFileChooser tempobjpatt = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        tempobjpatt.setDialogTitle("Select the .obj Pattern File:");
        tempobjpatt.setAcceptAllFileFilterUsed(false);
        tempobjpatt.addChoosableFileFilter(new FileNameExtensionFilter(".obj Files", "obj"));
        if(tempobjpatt.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) System.exit(0);
        
        JFileChooser selpatts = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        selpatts.setDialogTitle("Select the Shepards Pattern Files:");
        selpatts.setMultiSelectionEnabled(true);
        selpatts.setAcceptAllFileFilterUsed(false);
        selpatts.addChoosableFileFilter(new FileNameExtensionFilter(".txt Files", "txt"));
        if(selpatts.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) System.exit(0);
        
        JFileChooser selobjs = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        selobjs.setDialogTitle("Select the .obj Files to Base Comparisons on:");
        selobjs.setMultiSelectionEnabled(true);
        selobjs.setAcceptAllFileFilterUsed(false);
        selobjs.addChoosableFileFilter(new FileNameExtensionFilter(".obj Files", "obj"));
        if(selobjs.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) System.exit(0);
        
        JFileChooser targetfolder = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        targetfolder.setDialogTitle("Where to Save your Files:");
        targetfolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(targetfolder.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) System.exit(0);
        
        ObjReader temppoints = new ObjReader(tempobj.getSelectedFile().getPath());
        ObjReader temppatterns = new ObjReader(tempobjpatt.getSelectedFile().getPath());
        temppatterns.alphabetizeVertices();
        Variables vars = new Variables(selpatts.getSelectedFiles());
        
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        for(File file : selobjs.getSelectedFiles()){
            executor.execute(()->{
                try {
                    ObjReader objpoints = new ObjReader(file.getPath());
                    ICPRunner icp = new ICPRunner(objpoints, temppoints, temppatterns);
                    icp.runComparisons(40, 4);
                    
                    // comment out the line of code below and uncomment the one underneath if you do not want to utilize ICP
                    PointCompiler ptcomp = new PointCompiler(vars, icp);
                    //PointCompiler ptcomp = new PointCompiler(vars, objpoints, temppatterns);
                    
                    ptcomp.setallNeighbors();
                    ptcomp.setRegions();
                    ptcomp.setNeighbors();
                    ImgCompiler imgcomp = new ImgCompiler(ptcomp);
                    imgcomp.runShepardsMethod(0.75);
                    
                    // comment out the line of code below and uncomment the one underneath if you want to set bounds for comparing the Shepard Images
                    imgcomp.compShepardsMethod();
                    //imgcomp.compShepardsMethod(0.5);
                    
                    // comment out the line of code below and uncomment the one underneath if you do not want to utilize Shepard's method
                    ResultWriter writer = new ResultWriter(targetfolder.getSelectedFile().getPath(), imgcomp);
                    //ResultWriter writer = new ResultWriter(targetfolder.getSelectedFile().getPath(), ptcomp);
                } catch (Exception e){}
            });
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        
        JOptionPane.showMessageDialog(null, "Patterns Generated", "Complete", JOptionPane.INFORMATION_MESSAGE);
    }
}

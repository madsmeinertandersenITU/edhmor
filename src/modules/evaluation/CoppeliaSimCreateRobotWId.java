package modules.evaluation;

import apriltag.ImageResolution;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.visp.core.VpCameraParameters;

import apriltag.TagChecker;
import apriltag.TagUtils;
import coppelia.IntW;
import coppelia.remoteApi;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoppeliaSimCreateRobotWId extends CoppeliaSimCreateRobot implements Runnable {

    protected int tagCounter = 0;
    protected int[] moduleIDs;
    protected int[] moduleTagsTmp = null;
    private int referenceTag = 0;
    private int nextModuleID = -1;
    private int realCameraIndex = 0;

    protected CoppeliaSimulator coppeliaSimulator;
    protected TagChecker checker;

    protected VpCameraParameters realCamParameters;
    protected ImageResolution resolution;

    double initialHeight;
    double[] posIni;
    double[] poszero;

    protected boolean prompt = false;
    private Scanner input = new Scanner(System.in);

    private long initTime = System.currentTimeMillis(); //Time to measure the assembly time
    private double assemblyTime; //Assembly Time

    public int getTagCounter() {
        return tagCounter;
    }

    public void setTagCounter(int tagCounter) {
        this.tagCounter = tagCounter;
    }

    public int[] getModuleTags() {
        return moduleIDs;
    }

    public void setModuleTagsTmp(int[] moduleTags) {
        this.moduleTagsTmp = moduleTags;
    }

    public CoppeliaSimCreateRobotWId(remoteApi api, int clientID, double[] chromosomeDouble, String scene, boolean fP,
            boolean prompt) {
        super(api, clientID, chromosomeDouble, scene, fP);
        // TODO Auto-generated constructor stub
        this.prompt = prompt;
        initialHeight = (Math.abs(robotFeatures.getMinPos().z) + 0.001);
        posIni = new double[]{0, 0, initialHeight};
        poszero = new double[]{0, 0, 0};

        // FIXME: Careful! CoppeliaSimulator objects created with this method cannot launch
        // actual simulators or run them on clusters
        this.coppeliaSimulator = new CoppeliaSimulator();
        this.coppeliaSimulator.setCoppeliaSimApi(api);
        this.coppeliaSimulator.setClientID(clientID);

    }

    public CoppeliaSimCreateRobotWId(remoteApi api, int clientID, double[] chromosomeDouble, String scene, boolean fP,
            boolean prompt, int firstTag) {
        this(api, clientID, chromosomeDouble, scene, fP, prompt);
        this.referenceTag = firstTag;
        this.prompt = prompt;
    }

    public CoppeliaSimCreateRobotWId(CoppeliaSimulator simulator, double[] chromosomeDouble, String scene, boolean fP,
            boolean prompt, int firstTag, int realCameraIndex, VpCameraParameters realCamParameters, ImageResolution resolution) {
        this(simulator.getCoppeliaSimApi(), simulator.getClientID(), chromosomeDouble, scene, fP, prompt);
        this.coppeliaSimulator = simulator;
        this.referenceTag = firstTag;
        this.prompt = prompt;
        this.realCameraIndex = realCameraIndex;
        this.realCamParameters = realCamParameters;
        this.resolution = resolution;
    }

    protected String modelPath(int moduleType) {

        if (moduleIDs == null) {
            moduleIDs = new int[robotFeatures.getnModules()];
        }

        if (moduleTagsTmp != null) {
            if (tagCounter < moduleTagsTmp.length) {
                //We are using the module ids introduced
                nextModuleID = moduleTagsTmp[tagCounter];
            }
        }

        if (prompt) {
            System.out.println("Input module ID in position " + tagCounter + " of build");
            int n = Integer.parseInt(input.nextLine());
            moduleIDs[tagCounter] = n;
        } else {
            if (tagCounter == 0) {
                moduleIDs[tagCounter] = referenceTag;
            } else {
                if (nextModuleID != -1) {
                    moduleIDs[tagCounter] = nextModuleID;
                } else {
                    if (tagCounter < this.moduleIDs.length) {
                        moduleIDs[tagCounter] = tagCounter;
                    }
                }
            }

        }
        
        //FIXME: Get the id of the module, not of the tag
        if (tagCounter == 0) {
            moduleIDs[0] = TagUtils.Tag2DynamixelId(moduleIDs[0]);
        }

        String path = "models/edhmor/";
        path += moduleSet.getModuleSetName() + "/"; // moduleSetName
        path += moduleSet.getModuleName(moduleType) + "ID" + moduleIDs[tagCounter] + ".ttm"; // moduleName

        

        System.out.println("Module ids");
        for (int i = 0; i < moduleIDs.length; i++) {
            System.out.print(moduleIDs[i] + "_");
        }
        System.out.println();
        tagCounter++;
        return path;
    }

    public List<Integer> createRobotInteractively() {

        return createRobotInteractively(null);

    }

    public List<Integer> createRobotInteractively(int[] initialList) {

        loadScene();

        initAssembly(posIni, poszero);
        shiftBaseTemp(posIni, poszero, false); // (posIni,posZero, reset?)

        checker = new TagChecker(realCameraIndex, realCamParameters, resolution, coppeliaSimulator, referenceTag);

        if (initialList != null) {
            checker.setRealModuleTagsList(initialList);
            for (int i = 1; i < initialList.length; i++) {
                shiftBaseTemp(posIni, poszero, true);
                addAndConnectModule(i, posIni, poszero); // Calls modelPath which advances tagCounter
                shiftBaseTemp(posIni, poszero, false);
            }
        }
        initTime = System.currentTimeMillis();
        List<Integer> newModuleIDs;
        //= checker.getNextModuleIDWithVideo(true); // (firstTime?)
        boolean firstTime = true;
        System.out.println("STARTING WHILE LOOP");
        while (tagCounter < robotFeatures.getnModules()) {

            newModuleIDs = checker.getNextModuleIDWithVideo(firstTime);
            if (firstTime) {
                firstTime = false;
            }
            System.out.println("NEW TAGS FOUND: ");
            if (checker.isStop()) {
                break;
            }

            for (Iterator iterator = newModuleIDs.iterator(); iterator.hasNext();) {

                Integer integer = (Integer) iterator.next();

                if (tagCounter < robotFeatures.getnModules()) {
                    nextModuleID = integer.intValue(); // nextag is used by modelPath to correctly load the corresponding
                    System.out.println("NEXT TAG FOUND IS: " + nextModuleID);
                    // module
                    shiftBaseTemp(posIni, poszero, true);
                    addAndConnectModule(tagCounter, posIni, poszero); // Calls modelPath which advances tagCounter
                    shiftBaseTemp(posIni, poszero, false);

                    checker.waitUntilPositioned(nextModuleID);
                    System.out.println("TAG " + nextModuleID + " is positioned");

                    if (checker.isStop()) {
                        break;
                    }
                }

            }
            System.out.println("END WHILE LOOP");
        }

        finishAssembly(posIni, initialHeight);

        long stopTime = System.currentTimeMillis();
        assemblyTime = (stopTime - initTime) / 1000.0;
        System.out.println("Assembly Time: " + assemblyTime);
        checker.setAssembleCompleted(true);

        //Write assembly time
        FileOutputStream resultsFile = null;
        try {
            resultsFile = new FileOutputStream("C:/fai/documents/papers/2021_Frontiers_emerge/HPCResults/assemblyTime.txt", true);
            PrintStream printResults = new PrintStream(resultsFile);
            printResults.println("AssemblyTime: " + assemblyTime + " " + Instant.now() // Capture the current moment in UTC.
                    .truncatedTo(ChronoUnit.SECONDS) // Lop off the fractional second, as superfluous to our purpose.
                    .toString());
            printResults.close();
            resultsFile.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(HardwareEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HardwareEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Integer> finalModuleTags = checker.getRealModuleTagsList();

        while (!checker.isStop()) {
            checker.getNextModuleIDWithVideo(false); // Continue showing video until user closes program
        }
        return finalModuleTags;

    }

    @Override
    public void run() {
        createRobotInteractively(this.moduleTagsTmp);
    }

    public double getAssemblyTime() {
        return assemblyTime;
    }

}

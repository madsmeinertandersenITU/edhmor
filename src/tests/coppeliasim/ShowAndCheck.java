package tests.coppeliasim;

import apriltag.ImageResolution;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.visp.core.VpCameraParameters;

import apriltag.TagChecker;
import coppelia.IntW;
import coppelia.remoteApi;
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.evaluation.CoppeliaSimCreateRobotWId;
import modules.evaluation.CoppeliaSimulator;
import modules.util.ChromoConversion;
import modules.util.SimulationConfiguration;

public class ShowAndCheck {
    
    public static final int TAG_BASE = 0; // Apriltag id attached to the base
    public static final int CAMERA_ID = 0; // ID of the camera in OpenCV
    
    
    static {
        System.loadLibrary("visp_java331");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    private static TagChecker Checker;
    
    public static void main(String[] args) {
        
        double[] chromosomeDouble = ChromoConversion.str2double("[(0.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, "
                + "                                                4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "                                                0.0, 1.0, 2.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "                                                1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "                                                0.5455963591611365, 0.11371233636543576, 0.036290954196614544, 0.3903656017030154, 0.00679176572264828, 0.08146566559447732, 0.07535369694622207, 0.7730905441161025, 0.2500507281219915, 0.40313639025411774, 0.003973608347822122, 0.7781750911928096, 0.3899374167547881, 0.9600134640670561, 0.7250404836902793, 0.8445273695804674, 0.6467894800807368, 0.16030850687424925, 0.2018541954218076, 0.49567921698965856, 0.9019485291134028, 0.20829306079896515, 0.06612457688359719, 0.49610490207718816, 0.7488436794002526, 0.9645264481423355, 0.5163520317251132, 0.2457172378705993, 0.41377103759169165, 0.12842989434914487, 0.35238585296706193, 0.22133798867604992, 344.4679226211596, 309.281008589332, 266.8072439685561, 134.9338321211479, 12.159645687248428, 57.23409787318109, 232.8997112761175, 90.64154969188141, 121.74772800789312, 252.27177708222214, 66.17009812495084, 11.886793305793866, 11.792446546844415, 205.59952213863193, 79.29145620359417, 30.7604219708471, 0.4378857762257965, -0.4539603364820609, -0.018494123142167918, 0.49687190619165233, -0.43881005244237103, 0.39648466172584684, -0.304664078161781, -0.4413269361659312, 0.2201318672469349, -0.39377771322649235, -0.3754566640525431, -0.3969155009689903, 0.017185949030072756, -0.21401444995093033, 0.1949855550960401, -0.3794066650961495, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0] - 1.7976931348623157E308");
         
        
      //Set the correct module set to employ
        String moduleSet = "Emerge18AndFlatBaseModules";
        SimulationConfiguration.setModuleSet(moduleSet);
        ModuleSetFactory.reloadModuleSet();
        
        List<String> worldBase = new ArrayList<String>();
        worldBase.add("baseEstandar" + ".world");
        SimulationConfiguration.setWorldsBase(worldBase);
        
        CoppeliaSimulator coppeliaSim = new CoppeliaSimulator();
        System.out.println("connecting to CoppeliaSim...");
        coppeliaSim.connect2CoppeliaSim();
        
        remoteApi coppeliaSimApi = coppeliaSim.getCoppeliaSimApi();
        int clientID = coppeliaSim.getClientID();
        
        VpCameraParameters cam = new VpCameraParameters(548.53794929581727, 546.30027985268134,
                326.50908015822313, 257.39709106971588, -0.37491049674639482, 0.45591897981326202);
        
        CoppeliaSimCreateRobotWId robot = new CoppeliaSimCreateRobotWId(coppeliaSim, chromosomeDouble, "", false,false,TAG_BASE, CAMERA_ID,cam, ImageResolution.RES_640x480);//Last parameter: Base module tag number
        //robot.createRobot();
        List<Integer> moduleTags = robot.createRobotInteractively();


        
        //Checker = new TagChecker(CAMERA_ID, coppeliaSim, TAG_BASE);
        
        //Position Camera with photo        
        
       
        //Checker.setRealCamParams(cam);
        
        //Checker.positionCamera();
        
        //Check tag presence and positions
        //Checker.checkTagPositions(true);
        
        //Checker.showVideo();
        
        IntW pingTime = new IntW(0);
        coppeliaSimApi.simxGetPingTime(clientID, pingTime);

        coppeliaSim.disconnect();
        
    }
}

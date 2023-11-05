/* 
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2015 GII (UDC) and REAL (ITU)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package modules.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.evaluation.CoppeliaSimulator;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * SimulationConfiguration stores all the parameters of the simulation.
 * <p>
 * The parameters of the simulation are loaded, when the progam starts, from
 * simulationControl.xml. This file has to be placed in the same working
 * directory as the main program. It throws an exception if a parameter is not
 * well defined in the file.
 * <p>
 *
 * SimulationConfiguration.java Created on
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class SimulationConfiguration {
    
    //The evolutionary algorithm to use (config file)
    private static String eaConfigFile;

    //Parameters of the simulation
    private static double maxSimulationTime;
    private static int nAttempts;
    private static boolean useMPI;
    private static boolean useSingularity;
    private static String singularityPath;
    private static String coppeliaSimPath;
    private static int coppeliaSimStartingPort = 19997;//45000
    private static CoppeliaSimulator coppeliaSim = null;

    //Parametes of the modules 
    private static String moduleSet;

    //Parameters of the Robot
    private static int nMaxModulesIni;
    private static int nMinModulesIni; 
    private static boolean fistModulesBase;
    private static int firstNumConnections = 1000;
    private static int minTypeModules;
    private static int maxTypeModules;
    private static int nMaxConnections;

    //Parameters of the control
    private static String robotControllerStr;
    private static double maxPhaseControl;
    private static double minPhaseControl;
    private static double maxAmplitudeControl;
    private static double minAmplitudeControl;
    private static double maxAngularFreqControl;
    private static double minAngularFreqControl;

    private static boolean usePhaseControl;
    private static boolean useAmplitudeControl;
    private static boolean useAngularFControl;


    private static double noiseStd;
    private static boolean indivNoise;

    //various
    private static boolean debug;
    private static int jobId = 0;
    private static int maxModules = 1;

    //Parameters of the world/scene
    private static int numberOfWorldsBase = 1;
    private static List<String> worldsBase = new ArrayList<String>();
    private static String functionToEvaluateWorlds = "min";

    //Parameters of the fitness function
    private static double threshold = 0.0;
    private static double fitnessIncrease = 0.0;
    private static String fitnessFunctionStr;
    private static int fitnessFunctionIndicator = 0;
    private static double timeIniFitness = 5, timeEndFitness = -1;
    private static double penaltyFitness = 0.8;
    private static String poseFitness = "BASE";

    private static double assemblyTimePerModule = 200 / 9; //Taken from Frontiers article 2021
    
    static {
        try {

            XMLConfiguration config = new XMLConfiguration("./simulationControl.xml");
            
            //Evolutionary algorithm config file 
            SimulationConfiguration.eaConfigFile = config.getString("AlgorithmConfigFile");
            
            //Parameters of the module Set
            SimulationConfiguration.moduleSet = config.getString("ModuleSet");

            //Parameters of the simulation
            SimulationConfiguration.maxSimulationTime = config.getDouble("Simulation.MaxSimulationTime");
            SimulationConfiguration.nAttempts = config.getInt("Simulation.Attempts");
            SimulationConfiguration.useMPI = config.getBoolean("Simulation.UseMPI");
            SimulationConfiguration.useSingularity = config.getBoolean("Simulation.UseSingularity");
            if (SimulationConfiguration.useSingularity) {
                SimulationConfiguration.singularityPath = config.getString("Simulation.SingularitySifPath");
            }
            SimulationConfiguration.coppeliaSimPath = config.getString("Simulation.CoppeliaSim.Path");
            
            //Parameters of the Worlds (to evaluate the robots)
            SimulationConfiguration.numberOfWorldsBase = config.getInt("Worlds.NumberOfWorldsBase");
            for (int i = 0; i < numberOfWorldsBase; i++) {
                worldsBase.add(config.getString("Worlds.WorldBase" + i));
                if (worldsBase.get(i).isEmpty() || worldsBase.get(i) == null || worldsBase.get(i).equals("")) {
                    throw new Exception("Error loading the worlds/scenes: \n");
                }
            }
            SimulationConfiguration.functionToEvaluateWorlds = config.getString("Worlds.FunctionToEvaluateWorlds");

            //Parametros del control
            SimulationConfiguration.robotControllerStr = config.getString("Control.RobotController");
            SimulationConfiguration.maxPhaseControl = config.getDouble("Control.PhaseControl.MaxValue");
            SimulationConfiguration.minPhaseControl = config.getDouble("Control.PhaseControl.MinValue");
            SimulationConfiguration.maxAmplitudeControl = config.getDouble("Control.AmplitudeControl.MaxValue");
            SimulationConfiguration.minAmplitudeControl = config.getDouble("Control.AmplitudeControl.MinValue");
            SimulationConfiguration.maxAngularFreqControl = config.getDouble("Control.AngularFreqControl.MaxValue");
            SimulationConfiguration.minAngularFreqControl = config.getDouble("Control.AngularFreqControl.MinValue");

            SimulationConfiguration.usePhaseControl = config.getBoolean("Control.UsePhaseControl");
            SimulationConfiguration.useAmplitudeControl = config.getBoolean("Control.UseAmplitudeControl");
            SimulationConfiguration.useAngularFControl = config.getBoolean("Control.UseAngularFreqControl");


            SimulationConfiguration.noiseStd = config.getDouble("Control.noiseStd");
            SimulationConfiguration.indivNoise = config.getBoolean("Control.indivNoise");

            //Robot parameters
            SimulationConfiguration.nMaxModulesIni = config.getInt("Robot.NMaxModulesIni");
            SimulationConfiguration.nMinModulesIni = config.getInt("Robot.NMinModulesIni");
            SimulationConfiguration.fistModulesBase = config.getBoolean("Robot.FirstModuleBase");
            SimulationConfiguration.firstNumConnections = config.getInt("Robot.FirstNumConnections");
            SimulationConfiguration.minTypeModules = config.getInt("Robot.TypeModules.MinValue");
            SimulationConfiguration.maxTypeModules = config.getInt("Robot.TypeModules.MaxValue");
            SimulationConfiguration.nMaxConnections = config.getInt("Robot.NMaxConnections");

            //Fitness parameters

            fitnessFunctionStr = config.getString("FitnessFunction.Name");
            SimulationConfiguration.setFitnessFunctionStr(fitnessFunctionStr);
            SimulationConfiguration.timeIniFitness = config.getDouble("FitnessFunction.TimeIni");
            if (SimulationConfiguration.timeIniFitness > 0
                    && SimulationConfiguration.timeIniFitness > SimulationConfiguration.maxSimulationTime) {
                SimulationConfiguration.timeIniFitness = 0;
                System.err.println("Error: timeIniFitness is higher than maxSimulationTime, changing it to 0.");
            }
            SimulationConfiguration.timeEndFitness = config.getDouble("FitnessFunction.TimeEnd");
            if (SimulationConfiguration.timeEndFitness > 0
                    && SimulationConfiguration.timeEndFitness > SimulationConfiguration.maxSimulationTime) {
                SimulationConfiguration.timeEndFitness = SimulationConfiguration.maxSimulationTime;
                System.err.println("Error: timeEndFitness is lower than maxSimulationTime, changing it to maxSimulationTime.");
            }
            SimulationConfiguration.penaltyFitness = config.getDouble("FitnessFunction.Penalty");
            SimulationConfiguration.poseFitness = config.getString("FitnessFunction.Pose");

            //Various
            SimulationConfiguration.debug = config.getBoolean("Debug");
            SimulationConfiguration.assemblyTimePerModule = config.getDouble("Assembly.TimePerModule");

            config = new XMLConfiguration(SimulationConfiguration.getEaConfigFile());
            SimulationConfiguration.maxModules = (config.getInt("Population.Individual.Chromosome[@size]") + 3) / 9;

        } catch (Exception e) {
            //Error loading the parameters of the simulation
            System.err.println("Error loading the parameters of the simulation.");
            System.out.println(e);
            System.exit(-1);
        }
    }

    public SimulationConfiguration() {
    }

    public static boolean isFistModulesBase() {
        return fistModulesBase;
    }

    public static int getMaxTypeModules() {
        return maxTypeModules;
    }

    public static int getMinTypeModules() {
        return minTypeModules;
    }

    public static int getAttempts() {
        return nAttempts;
    }

    public static int getNMaxConnections() {
        return nMaxConnections;
    }

    public static double getMaxPhaseControl() {
        return maxPhaseControl;
    }

    public static double getMaxAmplitudeControl() {
        return maxAmplitudeControl;
    }

    public static double getMaxAngularFreqControl() {
        return maxAngularFreqControl;
    }

    public static double getMinAmplitudeControl() {
        return minAmplitudeControl;
    }

    public static double getMinAngularFreqControl() {
        return minAngularFreqControl;
    }

    public static double getMinPhaseControl() {
        return minPhaseControl;
    }

    public static int getFirstNumConnections() {
        return firstNumConnections;
    }

    public static boolean isUseMPI() {
        return useMPI;
    }

    public static int getNMaxModulesIni() {
        return nMaxModulesIni;
    }
    
    public static int getNMinModulesIni() {
        return nMinModulesIni;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean d) {
        debug = d;
    }

    public static int getJobId() {
        return jobId;
    }

    public static void setJobId(int jobId) {
        SimulationConfiguration.jobId = jobId;
    }

    public static int getMaxModules() {
        return maxModules;
    }

    public static void setMaxModules(int maxModules) {
        SimulationConfiguration.maxModules = maxModules;
    }

    public static int getFitnessFunction() {
        return fitnessFunctionIndicator;
    }

    public static String getFitnessFunctionStr() {
        return fitnessFunctionStr;
    }

    /*Depreciated. Legacy code for the gazebo simulator*/
    public static void setFitnessFunctionStr(String str) {
        fitnessFunctionStr = str;
        if (fitnessFunctionStr.contentEquals("useWalkDistance")) {
            fitnessFunctionIndicator = 0;
        } else if (fitnessFunctionStr.contentEquals("useWalkDistance45Degrees")) {
            fitnessFunctionIndicator = 1;
        } else if (fitnessFunctionStr.contentEquals("useWalkDistanceXMinusAbsY")) {
            fitnessFunctionIndicator = 2;
        } else if (fitnessFunctionStr.contentEquals("useWalkDistanceX")) {
            fitnessFunctionIndicator = 3;
        } else if (fitnessFunctionStr.contentEquals("useMaxHeight")) {
            fitnessFunctionIndicator = 10;
        } else if (fitnessFunctionStr.contentEquals("useMaxHeightWithLowTurns")) {
            fitnessFunctionIndicator = 11;
        } else if (fitnessFunctionStr.contentEquals("useMaxZMovement")) {
            fitnessFunctionIndicator = 20;
        } else if (fitnessFunctionStr.contentEquals("useMaxTurn")) {
            fitnessFunctionIndicator = 30;
        } else if (fitnessFunctionStr.contentEquals("useMaxTurnConCarga")) {
            fitnessFunctionIndicator = 31;
        } else if (fitnessFunctionStr.contentEquals("usePathZ")) {
            fitnessFunctionIndicator = 40;
        } else if (fitnessFunctionStr.contentEquals("useX-YWithoutFallStairs")) {
            fitnessFunctionIndicator = 50;
        } else if (fitnessFunctionStr.contentEquals("useCargaWithoutFall")) {
            fitnessFunctionIndicator = 60;
        } else if (fitnessFunctionStr.contentEquals("useCargaWithoutFallAndWithoutFallStairs")) {
            fitnessFunctionIndicator = 70;
        } else if (fitnessFunctionStr.contentEquals("usePaintWall")) {
            fitnessFunctionIndicator = 150;
        } else if (fitnessFunctionStr.contentEquals("useCleanFloor")) {
            fitnessFunctionIndicator = 200;
        }
//           else{
//            try {
//                throw new Exception("Wrong name for the fitness function!" +
//                        "fitnessFunctionStr: " + fitnessFunctionStr);
//            } catch (Exception ex) {
//                Logger.getLogger(SimulationConfiguration.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

    }

    public static List<String> getWorldsBase() {
        return worldsBase;
    }

    public static void setWorldsBase(List<String> worldsBase) {
        SimulationConfiguration.worldsBase = worldsBase;
    }

    public static String getFunctionToEvaluateWorlds() {
        return functionToEvaluateWorlds;
    }

    public static void setFunctionToEvaluateWorlds(String functionToEvaluateWorlds) {
        SimulationConfiguration.functionToEvaluateWorlds = functionToEvaluateWorlds;
    }

    public static boolean isUseAmplitudeControl() {
        return useAmplitudeControl;
    }

    public static void setUseAmplitudeControl(boolean useAmplitudeControl) {
        SimulationConfiguration.useAmplitudeControl = useAmplitudeControl;
    }

    public static boolean isUseAngularFControl() {
        return useAngularFControl;
    }

    public static void setUseAngularFControl(boolean useAngularFControl) {
        SimulationConfiguration.useAngularFControl = useAngularFControl;
    }

    public static boolean isUsePhaseControl() {
        return usePhaseControl;
    }

    public static void setUsePhaseControl(boolean usePhaseControl) {
        SimulationConfiguration.usePhaseControl = usePhaseControl;
    }

    public static String getModuleSet() {
        return moduleSet;
    }

    public static void setModuleSet(String moduleSet) {
        SimulationConfiguration.moduleSet = moduleSet;
    }

    public static double getMaxSimulationTime() {
        return maxSimulationTime;
    }

    public static void setMaxSimulationTime(double maxSimulationTime) {
        SimulationConfiguration.maxSimulationTime = maxSimulationTime;
    }

    public static String getCoppeliaSimPath() {
        return coppeliaSimPath;
    }

    public static int getCoppeliaSimStartingPort() {
        return coppeliaSimStartingPort;
    }

    public static void setCoppeliaSimStartingPort(int port) {
        SimulationConfiguration.coppeliaSimStartingPort = port;
    }

    public static double getTimeIniFitness() {
        return timeIniFitness;
    }

    public static void setTimeIniFitness(double timeIniFitness) {
        SimulationConfiguration.timeIniFitness = timeIniFitness;
    }

    public static double getTimeEndFitness() {
        return timeEndFitness;
    }

    public static void setTimeEndFitness(double timeEndFitness) {
        SimulationConfiguration.timeEndFitness = timeEndFitness;
    }

    public static double getPenaltyFitness() {
        return penaltyFitness;
    }

    public static void setPenaltyFitness(double penaltyFitness) {
        SimulationConfiguration.penaltyFitness = penaltyFitness;
    }

    public static String getPoseFitness() {
        return poseFitness;
    }

    public static void setPoseFitness(String poseFitness) {
        SimulationConfiguration.poseFitness = poseFitness;
    }

    public static CoppeliaSimulator getCoppeliaSim() {
        return coppeliaSim;
    }

    public static void setCoppeliaSim(CoppeliaSimulator sim) {
        SimulationConfiguration.coppeliaSim = sim;
    }

    public static String getRobotControllerStr() {
        return robotControllerStr;
    }

    public static boolean isUseSingularity() {
        return useSingularity;
    }

    public static String getSingularityPath() {
        return singularityPath;
    }

    public static double getNoiseStd() {
        // TODO Auto-generated method stub
        return noiseStd;
    }

    public static boolean getIndivNoise() {
        // TODO Auto-generated method stub
        return indivNoise;
    }

    public static double getAssemblyTimePerModule() {
        return assemblyTimePerModule;
    }

    public static String getEaConfigFile() {
        return eaConfigFile;
    }
}

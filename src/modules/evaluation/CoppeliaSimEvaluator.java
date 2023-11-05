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
package modules.evaluation;

import coppelia.IntW;
import coppelia.remoteApi;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import modules.control.ControllerFactory;
import modules.control.RobotController;
import modules.evaluation.dynamicFeatures.DynamicFeatures;
import modules.evaluation.dynamicFeatures.DynamicFeaturesEvaluator;
import modules.evaluation.fitness.FitnessFunction;
import modules.evaluation.fitness.FitnessFunctionFactory;
import modules.util.SimulationConfiguration;
import mpi.MPI;

/**
 *
 * @author fai
 */
public class CoppeliaSimEvaluator {

    private double chromosomeDouble[];
    List<Double> fitnessList = new ArrayList<Double>();
    List<Double> simulationTimeList = new ArrayList<Double>();
    private int nAttempts = 10;
    private int nModules;
    private boolean useMPI = false;
    private boolean guiOn = true;
    static Logger LOGGER = Logger.getLogger("failogger");
    //private int SERVER_ID = 0; // Numer of the CoppeliaSim server
    private double maxSimulationTime = 3;
    private double timeStartSim = 3;
    private int waitTimeLoadGazebo = 900;
    private int status = -1;
    private double timeStep = 0.05;
    private String scene = null;
    private String coppeliaSimComand;
    private CoppeliaSimCreateRobot robot;
    private RobotController controller;
    private FitnessFunction fitnessFunction;
    private DynamicFeaturesEvaluator dFeaturesEval;

    private CoppeliaSimulator coppeliaSimulator;
    private remoteApi coppeliaSimApi;
    private int clientID;
    private double time;
    private static int eval = 0;
    private int rank = 0;

    public void setGuiOn(boolean guiOn) {
        this.guiOn = guiOn;
    }

    public void setMaxSimulationTime(double number) {
        this.maxSimulationTime = number;
    }

    public CoppeliaSimEvaluator(double[] cromo) {
        this(cromo, "");
    }

    public CoppeliaSimEvaluator(double[] cromo, String scene) {
        this(cromo, scene, false);
    }

    public CoppeliaSimEvaluator(double[] cromo, String scene, boolean guiOn) {
        this.guiOn = guiOn;

        this.scene = scene;
        if (scene == null || scene.isEmpty() || scene.equals("")) {
            this.scene = SimulationConfiguration.getWorldsBase().get(0);
        }

        this.chromosomeDouble = cromo;

        if ((cromo.length + 3) % 9 == 0) {
            nModules = (cromo.length + 3) / 9;
        } else {
            if ((cromo.length + 3) % 6 == 0) {
                nModules = (cromo.length + 3) / 6;
            } else {
                if ((cromo.length + 3) % 5 == 0) {
                    nModules = (cromo.length + 3) / 5;
                } else {
                    System.err.println("CoppeliaSimEvaluator:");
                    System.err.println("Error in the number of modules nModules; cromo.length=" + cromo.length);
                    coppeliaSimulator.killEverythingAndExit();
                }
            }
        }
        this.getSimulationConfigurationParameters();

        if (useMPI) {
            int base = (SimulationConfiguration.getJobId() % 319) * 100;
//            if ((base + 100) > 65535)
//                base = (base + 100) % 65535;
        }

        if (SimulationConfiguration.isDebug()) {
            System.out.print("\n" + " Individual (CoppeliaSimEvaluator): ");
            for (int i = 0; i < this.chromosomeDouble.length; i++) {
                System.out.print(this.chromosomeDouble[i] + ", ");
            }
        }

        if (SimulationConfiguration.isUseMPI()) {
            rank = MPI.COMM_WORLD.Rank();
        }

        coppeliaSimulator = SimulationConfiguration.getCoppeliaSim();
        if (coppeliaSimulator == null) {
            System.out.println("coppeliaSim simulator is null, connecting to coppeliaSim...");
            coppeliaSimulator = new CoppeliaSimulator();
            coppeliaSimulator.start();
            coppeliaSimApi = coppeliaSimulator.getCoppeliaSimApi();
            SimulationConfiguration.setCoppeliaSim(coppeliaSimulator);
        }

        int attempt = 0;
        boolean success;
        do {
            success = prepareForEvaluation();
            if (!success) {
                System.out.println(rank + ": Building robot in simulator gave an "
                        + "error, attempt " + attempt);
                coppeliaSimulator.stop(); //Kill the CoppeliaSim process
                coppeliaSimulator.start();
                attempt++;
            }
        } while (!success && attempt < SimulationConfiguration.getAttempts());
        if (!success) {
            System.out.println(rank + ": We could not build the robot after "
                    + "several attempts. Finishing program.");
            coppeliaSimulator.killEverythingAndExit();
        }
    }

    private boolean prepareForEvaluation() {
        coppeliaSimApi = coppeliaSimulator.getCoppeliaSimApi();
        clientID = coppeliaSimulator.getClientID();

        robot = new CoppeliaSimCreateRobot(coppeliaSimApi, clientID, chromosomeDouble, this.scene, false, this.guiOn);
        boolean success = robot.createRobot();

        controller = ControllerFactory.getRobotController(coppeliaSimApi, clientID, robot);
        dFeaturesEval = new DynamicFeaturesEvaluator(coppeliaSimApi, clientID, robot);
        fitnessFunction = FitnessFunctionFactory.getFitnessFunction(coppeliaSimApi, clientID, robot, dFeaturesEval.getDynamicFeatures());
        return success;
    }

    public DynamicFeaturesEvaluator getdFeaturesEval() {
        return dFeaturesEval;
    }

    public double evaluate() {

        //TODO: Check CoppeliaSim server
        //TODO: Set CoppeliaSim GUI OFF or ON (if it is possible)
        //TODO: handle error in the evaluation  
        // enable the synchronous mode on the client:
        boolean success;
        int attempt = 0;
        do {
            success = true;
            coppeliaSimApi.simxSynchronous(clientID, true);
            // start the simulation:
            coppeliaSimApi.simxStartSimulation(clientID, remoteApi.simx_opmode_oneshot_wait);

            success &= this.fitnessFunction.init();
            success &= this.dFeaturesEval.init();

            // Now step a few times:
            int maxIter = (int) (maxSimulationTime / timeStep);
            for (int i = 0; i < maxIter; i++) {
                //Update joint position goals
                success &= controller.updateJoints(time);

                //Send trigger signal for the next step
                int ret = coppeliaSimApi.simxSynchronousTrigger(clientID);
                if (ret != remoteApi.simx_error_noerror && ret != remoteApi.simx_error_novalue_flag) {
                    System.out.println(rank + ": Error triggering the simulation; error=" + ret);
                    success = false;
                    break;
                }
                time += timeStep;

                success &= this.dFeaturesEval.update(time);
                success &= this.fitnessFunction.update(time);

                //Do not keep simulating if there was an error
                if (!success) {
                    break;
                }
            }
            if (success) {
                success &= this.dFeaturesEval.end();
                success &= this.fitnessFunction.end();
            }

            // stop the simulation:
            coppeliaSimApi.simxStopSimulation(clientID, remoteApi.simx_opmode_oneshot_wait);

            // Before closing the connection to V-REP, make sure that the last command sent out had time to arrive. You can guarantee this with (for example):
            IntW pingTime = new IntW(0);
            coppeliaSimApi.simxGetPingTime(clientID, pingTime);

            //Close the scene
            int iter = 0;
            int ret = coppeliaSimApi.simxCloseScene(clientID, remoteApi.simx_opmode_oneshot_wait);
            while (ret != remoteApi.simx_return_ok && iter < 3) {
                ret = coppeliaSimApi.simxCloseScene(clientID, remoteApi.simx_opmode_oneshot_wait);
                iter++;
            }
            if (ret != remoteApi.simx_return_ok) {
                System.err.println(rank + ": The scene has not been closed after 3 trials.");
                success = false;
            }
            if (success) {
                return this.fitnessFunction.getFitness();
            } else {
                System.out.println(rank + ": Attempting to reevaluate the individual, attempt " + attempt);
                coppeliaSimulator.stop(); //Kill the CoppeliaSim process
                coppeliaSimulator.start();
                prepareForEvaluation();
                System.out.println(rank + ": The new client ID is " + clientID);
                attempt++;
            }
        } while (!success && attempt < SimulationConfiguration.getAttempts());
        System.out.println(rank + ": Individual not evaluated correctly. error. Fitness is -1");
        coppeliaSimulator.killEverythingAndExit();
        return -1;
    }

    public CalculateModulePositions getRobotFeatures() {
        return this.robot.getRobotFeatures();
    }

    private void getSimulationConfigurationParameters() {

        try {
            this.maxSimulationTime = SimulationConfiguration.getMaxSimulationTime();
            this.useMPI = SimulationConfiguration.isUseMPI();
            this.nAttempts = SimulationConfiguration.getAttempts();

        } catch (Exception e) {
            LOGGER.severe("Error loading the control parameters of the simulation.");
            System.out.println(e);
            coppeliaSimulator.killEverythingAndExit();
        }

    }

    private double getInitialStepTime() {
        //TODO 
        return 0;
    }

    private void setStepTime(double timeStep) {
        this.timeStep = timeStep;
    }

    public void setUpdateRate(double uRate) {
    }

    public void getCoppeliaSimPing() {
        IntW pingTime = new IntW(0);
        coppeliaSimApi.simxGetPingTime(clientID, pingTime);
    }

    public DynamicFeatures getDynamicFeatures() {
        return this.dFeaturesEval.getDynamicFeatures();
    }

}

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
package dynamixel;

import coppelia.remoteApi;
import java.util.ArrayList;
import java.util.List;
import modules.ModuleSetFactory;
import modules.evaluation.CalculateModulePositions;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.evaluation.CoppeliaSimulator;
import modules.evaluation.staticFeatures.SymmetryFeature;
import modules.evaluation.staticFeatures.YSymmetryEvaluator;
import modules.individual.String2Tree;
import modules.individual.TreeIndividual;
import modules.util.ChromoConversion;
import modules.util.SimulationConfiguration;

/**
 * DynamixelSinusoidalController.java Created on 24/03/2021
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class DynamixelSinusoidalController extends DynamixelController implements Runnable {

    boolean usePhaseControl = SimulationConfiguration.isUsePhaseControl();
    boolean useAngularFreqControl = SimulationConfiguration.isUseAngularFControl();
    boolean useAmplitudeControl = SimulationConfiguration.isUseAmplitudeControl();

    int[] phaseControl, moduleType;
    double[] amplitudeControl, andularFreqControl;

    double[] maxAmplitude = new double[SimulationConfiguration.getMaxModules()]; //FIXME: could have more control parameters than actuators with modules with more dof
    double[] maxAngFreq = new double[SimulationConfiguration.getMaxModules()];   //FIXME: could have more control parameters than actuators with modules with more dof

    byte[] moduleId = new byte[SimulationConfiguration.getMaxModules()];//FIXME: a module could have more actuators than one in modules with more dof

    int nModules = 0;
    boolean emergencyStop = false;

    public DynamixelSinusoidalController(CoppeliaSimCreateRobot robot) {
        super();

        phaseControl = robot.getPhaseControl();
        moduleType = robot.getModuleType();
        amplitudeControl = robot.getAmplitudeControl();
        andularFreqControl = robot.getAngularFreqControl();

        nModules = robot.getRobotFeatures().getnModules();
        for (int module = 0; module < nModules; module++) {
            //FIXME: could have more control parameters than actuators with modules with more dof
            maxAmplitude[module] = ModuleSetFactory.getModulesSet().getModulesMaxAmplitude(moduleType[module]);
            maxAngFreq[module] = ModuleSetFactory.getModulesSet().getModulesMaxAngularFrequency(moduleType[module]);
        }
    }

    public void goToInitPos() {
        updateJoints(0);
    }

    public void goToPos(double time) {
        updateJoints(time);
    }

    public void updateJoints(double time) {

        for (int module = 0; module < nModules; module++) {
            //TODO: this iterates for each module but we should also iterate for 
            //each actuator in the module. For now, just suppose that all the
            //modules have only one dof.

            //If this module is passive, there is no motor to update
            if (moduleId[module] == 0) {
                continue;
            }

            float targetPosition;
            if (usePhaseControl && !useAngularFreqControl && !useAmplitudeControl) {
                targetPosition = (float) (maxAmplitude[module] * Math.sin(maxAngFreq[module] * time + phaseControl[module] / 180. * Math.PI));
            } else {
                double amplitude = maxAmplitude[module] * amplitudeControl[module];
                double angularFreq = maxAngFreq[module] * andularFreqControl[module];
                targetPosition = (float) (amplitude * Math.sin(angularFreq * time + phaseControl[module] / 180. * Math.PI));
            }

            short offset = (short) 512;
            short goalPos = (short) (offset + targetPosition / Math.PI * 512);

            //Instead of setting the target pos, one by one, we send them all at once
            //setTargetPos(moduleId[module], goalPos);
            setTargetPosBulkWrite(moduleId[module], goalPos);
            //System.out.println("Writing ID: " + moduleId[module] + " Goal Pos: " + goalPos);

        }
        sendTargetPos();
    }
    
    public void moveTo0Degrees() {

        for (int module = 0; module < nModules; module++) {
            //TODO: this iterates for each module but we should also iterate for 
            //each actuator in the module. For now, just suppose that all the
            //modules have only one dof.

            //If this module is passive, there is no motor to update
            if (moduleId[module] == 0) {
                continue;
            }


            short goalPos = (short) 512;
            
            //Instead of setting the target pos, one by one, we send them all at once
            //setTargetPos(moduleId[module], goalPos);
            setTargetPosBulkWrite(moduleId[module], goalPos);
            //System.out.println("Writing ID: " + moduleId[module] + " Goal Pos: " + goalPos);

        }
        sendTargetPos();
    }

    public void enableTorque(boolean enable) {

        for (int module = 0; module < nModules; module++) {
            //TODO: this iterates for each module but we should also iterate for 
            //each actuator in the module. For now, just suppose that all the
            //modules have only one dof.

            //If this module is not passive, enable disable the torque
            if (moduleId[module] != 0) {
                enableTorque(moduleId[module], enable);
            }
        }
    }

    public void setModuleId(byte[] moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public void run() {
        evaluate();
    }

    public void evaluate() {
        System.out.println("Starting the evaluation...");
        double time = 0;
        double maxTime = SimulationConfiguration.getMaxSimulationTime()*1000;
        long timeInit = System.currentTimeMillis();
        int iter = 0;
        while (time < maxTime) {
            if(emergencyStop)
                break;
            updateJoints(time/1000.0);
            iter++;
            time=System.currentTimeMillis()-timeInit;
            while (time < iter*50) {
                time=System.currentTimeMillis()-timeInit;
            }
        }
        System.out.println("Evaluation ended...");
    }

    public void setEmergencyStop(boolean emergencyStop) {
        this.emergencyStop = emergencyStop;
    }
    
    public boolean getEmergencyStop() {
        return emergencyStop;
    }

    public static void main(String[] args) {
        System.out.println("Start!");

        double[] chromosomeDouble = ChromoConversion.str2double("[(0.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, "
                + "                                                4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "                                                0.0, 1.0, 2.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "                                                1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "                                                0.5455963591611365, 0.11371233636543576, 0.036290954196614544, 0.3903656017030154, 0.00679176572264828, 0.08146566559447732, 0.07535369694622207, 0.7730905441161025, 0.2500507281219915, 0.40313639025411774, 0.003973608347822122, 0.7781750911928096, 0.3899374167547881, 0.9600134640670561, 0.7250404836902793, 0.8445273695804674, 0.6467894800807368, 0.16030850687424925, 0.2018541954218076, 0.49567921698965856, 0.9019485291134028, 0.20829306079896515, 0.06612457688359719, 0.49610490207718816, 0.7488436794002526, 0.9645264481423355, 0.5163520317251132, 0.2457172378705993, 0.41377103759169165, 0.12842989434914487, 0.35238585296706193, 0.22133798867604992, 344.4679226211596, 309.281008589332, 266.8072439685561, 134.9338321211479, 12.159645687248428, 57.23409787318109, 232.8997112761175, 90.64154969188141, 121.74772800789312, 252.27177708222214, 66.17009812495084, 11.886793305793866, 11.792446546844415, 205.59952213863193, 79.29145620359417, 30.7604219708471, 0.4378857762257965, -0.4539603364820609, -0.018494123142167918, 0.49687190619165233, -0.43881005244237103, 0.39648466172584684, -0.304664078161781, -0.4413269361659312, 0.2201318672469349, -0.39377771322649235, -0.3754566640525431, -0.3969155009689903, 0.017185949030072756, -0.21401444995093033, 0.1949855550960401, -0.3794066650961495, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0] - 1.7976931348623157E308");

        CoppeliaSimulator simulator = new CoppeliaSimulator();
        System.out.println("connecting to CoppeliaSim...");
        simulator.connect2CoppeliaSim();

        remoteApi coppeliaSimApi = simulator.getCoppeliaSimApi();
        int clientID = simulator.getClientID();

        CoppeliaSimCreateRobot robot = new CoppeliaSimCreateRobot(coppeliaSimApi, clientID, chromosomeDouble, "", false);
        robot.createRobot();

        DynamixelSinusoidalController controller = new DynamixelSinusoidalController(robot);

        //Create moduleId based on modules found, justvto test, not right order
        List<Byte> ids = controller.scan();
        byte[] moduleId = new byte[SimulationConfiguration.getMaxModules()];
        for (int i = 0; i < ids.size(); i++) {
            moduleId[i + 1] = ids.get(i);
        }

        controller.setModuleId(moduleId);
        controller.enableTorque(true);

        long timeInit = System.currentTimeMillis();
        while (true) {

            controller.updateJoints(System.currentTimeMillis() / 1000.0);

            if (System.currentTimeMillis() - timeInit > 20000) {
                break;
            }
        }

        controller.close();

        return;
    }
}

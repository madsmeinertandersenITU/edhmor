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
package modules;

import es.udc.gii.common.eaf.algorithm.population.Individual;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3d;
import modules.evaluation.CalculateModulePositions;
import modules.jeaf.application.edhmor.RoboticObjetiveFunction;

/**
 *
 * @author fai
 */
public class RobotFeatures {

    //Number of different modules in the population
    private int nModules = 0;
    
    private double[] typePerc;
           
    private double mass = 0;    //Mass of the population
    
    //Average of the dimensions of the robots in the population
    private double meanDimX = 0, meanDimY = 0, meanDimZ = 0;   
    
    //Standard dispersion of the robots in the population
    private double dispDimX = 0, dispDimY = 0, dispDimZ = 0;    
    
    //dimensions of the robots in the population
    private List<Vector3d> dimensiones = new ArrayList();
    
    //Average and standard dispersion of the moment of inertia
    private double meanIx = 0, meanIy = 0, meanIz = 0;
    private double dispIx = 0, dispIy = 0, dispIz = 0;
    private List<Vector3d> inertia = new ArrayList();
    
    //connection per module
    private double meanConnectionsPerModule = 0, dispConnectionsPerModule = 0,
            meanDispConnectionsPerModule = 0;
    private List<Double> connectionsPerModule = new ArrayList();
    
    private int nBrokenConn = 0;

    public RobotFeatures(List<Individual> individuals) {
        typePerc = new double[ModuleSetFactory.getModulesSet().getModulesTypeNumber()];
        for (int i = 0; i < typePerc.length; i++) {
            typePerc[i] = 0;
        }
        
        for (Individual ind : individuals) {
            CalculateModulePositions robot = new CalculateModulePositions(ind.getChromosomeAt(0));
            nModules += robot.getnModules();
            double[] typePercAux = robot.getTypePercentage();
            for (int i = 0; i < typePercAux.length; i++) {
                typePerc[i] += typePercAux[i];
            }

            mass += robot.getRobotMass();

            meanDimX += robot.getDimensions().x;
            meanDimY += robot.getDimensions().y;
            meanDimZ += robot.getDimensions().z;
            Vector3d dimTemp = new Vector3d(robot.getDimensions().x, robot.getDimensions().y, robot.getDimensions().z);
            dimensiones.add(dimTemp);

            meanIx += robot.getInertia().getX();
            meanIy += robot.getInertia().getY();
            meanIz += robot.getInertia().getZ();
            inertia.add(new Vector3d(robot.getInertia().getX(), robot.getInertia().getY(), robot.getInertia().getZ()));

            meanConnectionsPerModule += robot.getAverageConnectionsPerModule();
            connectionsPerModule.add(robot.getAverageConnectionsPerModule());
            meanDispConnectionsPerModule += robot.getDispersionConnectionsPerModule();
            
            nBrokenConn += ind.getObjectives().get(RoboticObjetiveFunction.OBJECTIVE_BROCKEN_CONN_WORLD_1);
        }

        //Calculate mean values
        nModules /= individuals.size();
        for (int i = 0; i < typePerc.length; i++) {
            typePerc[i] /= individuals.size();
        }

        mass /= individuals.size();
        meanDimX /= individuals.size();
        meanDimY /= individuals.size();
        meanDimZ /= individuals.size();
        meanIx /= individuals.size();
        meanIy /= individuals.size();
        meanIz /= individuals.size();
        
        meanConnectionsPerModule /= individuals.size();
        meanDispConnectionsPerModule /= individuals.size();

        for (Vector3d d : dimensiones) {
            dispDimX += Math.pow(d.x - meanDimX, 2);
            dispDimY += Math.pow(d.y - meanDimY, 2);
            dispDimZ += Math.pow(d.z - meanDimZ, 2);
        }
        dispDimX = Math.sqrt(dispDimX) / individuals.size();
        dispDimY = Math.sqrt(dispDimY) / individuals.size();
        dispDimZ = Math.sqrt(dispDimZ) / individuals.size();

        for (Vector3d i : inertia) {
            dispIx += Math.pow(i.x - meanIx, 2);
            dispIy += Math.pow(i.y - meanIy, 2);
            dispIz += Math.pow(i.z - meanIz, 2);
        }
        dispIx = Math.sqrt(dispIx) / individuals.size();
        dispIy = Math.sqrt(dispIy) / individuals.size();
        dispIz = Math.sqrt(dispIz) / individuals.size();
        
        for (Double d : connectionsPerModule){
            this.dispConnectionsPerModule = Math.pow(d - this.meanConnectionsPerModule, 2);
        }
        dispConnectionsPerModule = Math.sqrt(dispIx) / individuals.size();
        
        nBrokenConn /= individuals.size();
    }

    public int getnModulos() {
        return nModules;
    }

    public double[] getTypePerc() {
        return typePerc;
    }
    
    public double getMass() {
        return mass;
    }

    public double getMeanDimX() {
        return meanDimX;
    }

    public double getMeanDimY() {
        return meanDimY;
    }

    public double getMeanDimZ() {
        return meanDimZ;
    }

    public double getDispDimX() {
        return dispDimX;
    }

    public double getDispDimY() {
        return dispDimY;
    }

    public double getDispDimZ() {
        return dispDimZ;
    }

    public double getMeanIx() {
        return meanIx;
    }

    public double getMeanIy() {
        return meanIy;
    }

    public double getMeanIz() {
        return meanIz;
    }

    public double getDispIx() {
        return dispIx;
    }

    public double getDispIy() {
        return dispIy;
    }

    public double getDispIz() {
        return dispIz;
    }

    public double getMeanConnectionsPerModule() {
        return meanConnectionsPerModule;
    }

    public double getDispConnectionsPerModule() {
        return dispConnectionsPerModule;
    }

    public double getMeanDispConnectionsPerModule() {
        return meanDispConnectionsPerModule;
    }

    public int getMeanBrokenConn() {
        return nBrokenConn;
    }
    
    
}

/* 
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) REAL (ITU)
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

import apriltag.ImageResolution;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.evaluation.CoppeliaSimulator;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.visp.core.VpCameraParameters;

/**
 * SimulationConfiguration stores all the parameters of the physical setup:
 * manipulator, cameras, etc.
 * <p>
 * The parameters of the simulation are loaded, when the progam starts, from
 * physicalSetupParameters.xml. This file has to be placed in the same working
 * directory as the main program. It throws an exception if a parameter is not
 * well defined in the file.
 * <p>
 *
 * PhysicalSetupConfiguration.java Created on 2/09/2022
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class PhysicalSetupConfiguration {

    private static boolean simulation;
    private static int arenaCeilingCamIndex;
    private static String arenaCeilingCamResolution;
    private static Mat arenaDistParam;
    private static Mat arenaIntrinsicParam;
    private static int ur5EndEffectorCamIndex;
    private static String ur5EndEffectorCamResolution;
    private static double tableHeight;
    private static ExtendedXMLConfiguration config;

    static {
        try {

            PhysicalSetupConfiguration.config = new ExtendedXMLConfiguration("./physicalSetupParameters.xml");

            PhysicalSetupConfiguration.simulation = PhysicalSetupConfiguration.config.getBoolean("UseSimulation");

            PhysicalSetupConfiguration.arenaCeilingCamIndex = PhysicalSetupConfiguration.config.getInt("ArenaCeilingCamera.Index");
            PhysicalSetupConfiguration.arenaCeilingCamResolution = PhysicalSetupConfiguration.config.getString("ArenaCeilingCamera.Resolution");
            PhysicalSetupConfiguration.ur5EndEffectorCamIndex = PhysicalSetupConfiguration.config.getInt("UR5EndEffectorCamera.Index");
            PhysicalSetupConfiguration.ur5EndEffectorCamResolution = PhysicalSetupConfiguration.config.getString("UR5EndEffectorCamera.Resolution");
            PhysicalSetupConfiguration.tableHeight = PhysicalSetupConfiguration.config.getDouble("TableHeight");

        } catch (ConfigurationException e) {
            //Error loading the parameters of the simulation
            System.err.println("Error loading the parameters of the physical setup.");
            System.out.println(e);
            System.exit(-1);
        }
    }

    public static Mat getArenaCeilingDistParam() {
        Mat distParam = new Mat(1, 5, CvType.CV_64FC1);
        distParam.put(0, 0, PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.DistorsionParameters.k1"));
        distParam.put(0, 1, PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.DistorsionParameters.k2"));
        distParam.put(0, 2, PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.DistorsionParameters.p1"));
        distParam.put(0, 3, PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.DistorsionParameters.p2"));
        distParam.put(0, 4, PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.DistorsionParameters.k3"));
        return distParam;
    }

    public static Mat getArenaCeilingIntrinsicCamParam() {
        Mat intrinsicParam = new Mat(3, 3, CvType.CV_64FC1);
        intrinsicParam.put(0, 0, PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.IntrinsicParameters.fx"));
        intrinsicParam.put(0, 1, 0.0);
        intrinsicParam.put(1, 0, 0.0);
        intrinsicParam.put(1, 1, PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.IntrinsicParameters.fy"));
        intrinsicParam.put(0, 2, PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.IntrinsicParameters.cx"));
        intrinsicParam.put(1, 2, PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.IntrinsicParameters.cy"));
        intrinsicParam.put(2, 0, 0.0);
        intrinsicParam.put(2, 1, 0.0);
        intrinsicParam.put(2, 2, 1.0);
        return intrinsicParam;
    }
    
    public static Mat getArenaCeilingOptimizedCamParam() {
        Mat optimizedParam = new Mat(3, 3, CvType.CV_64FC1);
        optimizedParam.put(0, 0, PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.OptimizedParameters.fx"));
        optimizedParam.put(0, 1, 0.0);
        optimizedParam.put(1, 0, 0.0);
        optimizedParam.put(1, 1, PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.OptimizedParameters.fy"));
        optimizedParam.put(0, 2, PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.OptimizedParameters.cx"));
        optimizedParam.put(1, 2, PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.OptimizedParameters.cy"));
        optimizedParam.put(2, 0, 0.0);
        optimizedParam.put(2, 1, 0.0);
        optimizedParam.put(2, 2, 1.0);
        return optimizedParam;
    }

    public static VpCameraParameters getArenaCeilingIntrinsicVispCamParam() {
        VpCameraParameters camParam = new VpCameraParameters(
                PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.IntrinsicParameters.fx"),
                PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.IntrinsicParameters.fy"),
                PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.IntrinsicParameters.cx"),
                PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.IntrinsicParameters.cy"));
        return camParam;
    }
    
    public static VpCameraParameters getArenaCeilingOptimizedVispCamParam() {
        VpCameraParameters camParam = new VpCameraParameters(
                PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.OptimizedParameters.fx"),
                PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.OptimizedParameters.fy"),
                PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.OptimizedParameters.cx"),
                PhysicalSetupConfiguration.config.getDouble("ArenaCeilingCamera.OptimizedParameters.cy"));
        return camParam;
    }
    

    public static void saveArenaCeilingCameraParameters(Mat distParam, Mat intrinciscParam, Mat optimizedParam) {
        PhysicalSetupConfiguration.config.setProperty("ArenaCeilingCamera.DistorsionParameters.k1", distParam.get(0, 0));
        PhysicalSetupConfiguration.config.setProperty("ArenaCeilingCamera.DistorsionParameters.k2", distParam.get(0, 1));
        PhysicalSetupConfiguration.config.setProperty("ArenaCeilingCamera.DistorsionParameters.p1", distParam.get(0, 2));
        PhysicalSetupConfiguration.config.setProperty("ArenaCeilingCamera.DistorsionParameters.p2", distParam.get(0, 3));
        PhysicalSetupConfiguration.config.setProperty("ArenaCeilingCamera.DistorsionParameters.k3", distParam.get(0, 4));

        PhysicalSetupConfiguration.config.setProperty("ArenaCeilingCamera.IntrinsicParameters.fx", intrinciscParam.get(0, 0));
        PhysicalSetupConfiguration.config.setProperty("ArenaCeilingCamera.IntrinsicParameters.fy", intrinciscParam.get(1, 1));
        PhysicalSetupConfiguration.config.setProperty("ArenaCeilingCamera.IntrinsicParameters.cx", intrinciscParam.get(0, 2));
        PhysicalSetupConfiguration.config.setProperty("ArenaCeilingCamera.IntrinsicParameters.cy", intrinciscParam.get(1, 2));
        
        PhysicalSetupConfiguration.config.setProperty("ArenaCeilingCamera.OptimizedParameters.fx", optimizedParam.get(0, 0));
        PhysicalSetupConfiguration.config.setProperty("ArenaCeilingCamera.OptimizedParameters.fy", optimizedParam.get(1, 1));
        PhysicalSetupConfiguration.config.setProperty("ArenaCeilingCamera.OptimizedParameters.cx", optimizedParam.get(0, 2));
        PhysicalSetupConfiguration.config.setProperty("ArenaCeilingCamera.OptimizedParameters.cy", optimizedParam.get(1, 2));
        
        try {
            PhysicalSetupConfiguration.config.save();
        } catch (ConfigurationException ex) {
            Logger.getLogger(PhysicalSetupConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean isSimulation() {
        return simulation;
    }

    public static int getArenaCeilingCamIndex() {
        return arenaCeilingCamIndex;
    }

    public static ImageResolution getArenaCeilingCamResolution() {
        return ImageResolution.valueOf("RES_" + arenaCeilingCamResolution);
    }

    public static int getUR5EndEffectorCamIndex() {
        return ur5EndEffectorCamIndex;
    }

    public static ImageResolution getUR5EndEffectorCamResolution() {
        return ImageResolution.valueOf("RES_" + ur5EndEffectorCamResolution);
    }

    public static double getTableHeight() {
        return tableHeight;
    }
    
}

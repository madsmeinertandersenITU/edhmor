/*
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2016 GII (UDC) and REAL (ITU)
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

import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.remoteApi;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.jeaf.application.edhmor.MainClass;
import modules.util.ModuleRotation;
import modules.util.SimulationConfiguration;
import mpi.MPI;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * CoppeliaSimulator.java Created on 27/03/2016
 *
 * @author Andres Faiña <anfv at itu.dk>
 */
public class CoppeliaSimulator {

    Process simulator = null;
    StreamGobbler errorGobbler = null;
    StreamGobbler outputGobbler = null;
    private int rank = 0, clientID = -1;
    private remoteApi coppeliaSimApi = null;
    private int port = SimulationConfiguration.getCoppeliaSimStartingPort();
    private int attempt = 0;
    private boolean guiOn = false;

    public void start() {
        this.start(19997);
    }

    public void start(int jobId) {
        if (SimulationConfiguration.isUseMPI()) {
            rank = MPI.COMM_WORLD.Rank();
        }

        // Try the port by default (trying to connect to running simulator)
        port = SimulationConfiguration.getCoppeliaSimStartingPort();
        if (SimulationConfiguration.isUseMPI()) {
            port += MPI.COMM_WORLD.Rank();
            // port*= -1;
        }

        // Try to connect to an existing open CoppeliaSimulator
        connect2CoppeliaSim();

        while (clientID == -1 && attempt < SimulationConfiguration.getAttempts() + 1) {
            System.out.println("PROCESS " + rank + ": Failed connecting to remote API server on port " + port
                    + "; attempt: " + attempt);

            // Call stop simulator using old port number
            stop();

            attempt++;
            int nSimulators = 1;
            if (SimulationConfiguration.isUseMPI()) {
                nSimulators = MPI.COMM_WORLD.Size();
            }

            // Update the port of the simulator and launch and connect again
            port += nSimulators * (attempt + 1);
            System.out.println("PROCESS " + rank + ": Launching CoppeliaSim and restarting remote API"
                    + " server on port " + port + ", nSimulators: " + nSimulators + ", attempt: " + attempt);
            launchCoppeliaSim(jobId);
            connect2CoppeliaSim();

        }
        if (clientID == -1) {
            System.out.println("PROCESS " + rank + ": Program ended! Failed to connect to CoppeliaSim on port " + port
                    + "; after " + attempt + " attempts. ");
            killEverythingAndExit();
        } else {
            System.out.println("PROCESS " + rank + ": Simulator launched and connected in attempt: " + attempt);
        }

        coppeliaSimApi.simxSynchronous(clientID, true);

    }

    public void killEverythingAndExit() {
        MPI.COMM_WORLD.Abort(-1);// Try to close all the programs
        pkillCoppeliaSim();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
        }

        pkillJava();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(-1);
    }

    public void launchCoppeliaSim(int jobId) {

        if (simulator == null) {
            List<String> processArguments = new ArrayList<String>();

            String coppeliaSimPath = System.getenv("COPPELIASIM_HOME");
            System.out.println("PROCESS " + rank + ": CoppeliaSim path: " + coppeliaSimPath);
            System.out.println("PROCESS " + rank + ": Setting the port to: " + port);

            if (SystemUtils.IS_OS_WINDOWS) {
                processArguments.add(coppeliaSimPath + "\\coppeliaSim.exe");
            } else {
                // SystemUtils.IS_OS_LINUX
                processArguments.add("stdbuf");
                processArguments.add("-o0");
                processArguments.add("-e0");
                processArguments.add("-i0");
                if (SimulationConfiguration.isUseSingularity()) {
                    processArguments.add("singularity");
                    processArguments.add("exec");
                    processArguments.add(SimulationConfiguration.getSingularityPath());
                }

                // Now, we use this line in the .bashrc file
                // export QT_QPA_PLATFORM=offscreen
                // Therefore, we not need to use xvfb-run
                // if (SimulationConfiguration.isUseMPI()) {
                // processArguments.add("xvfb-run");
                // processArguments.add("-a");
                // }
                // We call directly CoppeliaSim and not soppeliasim.sh
                // Be sure that the COPPELIASIM_HOME is added to LD_LIBRARY_PATH
                // processArguments.add(System.getenv("COPPELIASIM_HOME") + "/coppeliaSim");
                processArguments.add(System.getenv("COPPELIASIM_HOME") + "/coppeliaSim");
            }

            if (!guiOn) {
                processArguments.add("-h");
            }
            processArguments.add("-gREMOTEAPISERVERSERVICE_" + port + "_FALSE_TRUE");

            /* Initialize a v-rep simulator based on the starNumber parameter */
            try {
                System.out.println("PROCESS " + rank + ": Starting simulator with arguments: " + processArguments);
                ProcessBuilder qq = new ProcessBuilder(processArguments);// ,
                                                                         // "-h"/home/rodr/EvolWork/Modular/Maze/MazeBuilderR01.ttt");

                qq.directory(new File(coppeliaSimPath));
                qq.redirectErrorStream(true); // Error and std output redirected to the same pipe

                // CoppeliaSim error and standart output redirected to the output of the
                // program. To redirect to a file use these line
                File log = new File("coppeliaSim_output.txt");
                qq.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
                // qq.redirectOutput(ProcessBuilder.Redirect.INHERIT);

                simulator = qq.start();
                // outputGobbler = new StreamGobbler(simulator.getInputStream(), "COPPELIASIM_"
                // + port + "_OUTPUT");
                // errorGobbler = new StreamGobbler(simulator.getErrorStream(), "COPPELIASIM_" +
                // port + "_ERROR");
                // kick them off
                // errorGobbler.start();
                // outputGobbler.start();

                // if (MPI.COMM_WORLD.Rank() == 0) {
                Thread.sleep(10000); // wait for the coppeliaSim Api simulators
                // }

            } catch (IOException e) {
                System.out.println(e.toString());
            } catch (InterruptedException e) {
                System.out.println(e.toString());
            }
        }
    }

    public void disconnect() {
        coppeliaSimApi.simxFinish(clientID);
    }

    public void connect2CoppeliaSim() {
        System.out.println("PROCESS " + rank + ": Connect2CoppeliaSim");
        coppeliaSimApi = new remoteApi();
        coppeliaSimApi.simxFinish(-1); // just in case, close all opened connections
        clientID = coppeliaSimApi.simxStart("127.0.0.1", port, true, false, 5000, 5);
        if (clientID == -1) {
            System.out.println("PROCESS " + rank + ": CoppeliaSim connection was NOT started in port " + port);
        } else {
            System.out.println("PROCESS " + rank + ": CoppeliaSim connection started in port " + port
                    + " with clientID " + clientID);
            IntW pingTime = new IntW(0);
            coppeliaSimApi.simxGetPingTime(clientID, pingTime);
            System.out.println("PROCESS " + rank + ": Ping Time (1): " + pingTime.getValue());
            coppeliaSimApi.simxGetPingTime(clientID, pingTime);
            System.out.println("PROCESS " + rank + ": Ping Time (2): " + pingTime.getValue());
            IntWA objectHandles = new IntWA(1);
            int ret = coppeliaSimApi.simxGetObjects(clientID, coppeliaSimApi.sim_handle_all, objectHandles,
                    coppeliaSimApi.simx_opmode_blocking);
            System.out.println("PROCESS " + rank + ": Objects  in scene: " + objectHandles.getArray().length
                    + ", return code: " + ret);
        }
    }

    public void stop() {

        System.out.println("PROCESS " + rank + ": Stopping CoppeliaSim at port : " + port);
        // First close the connection to V-REP:

        int exitStatus = -1000;
        if (simulator != null) {
            try {
                if (simulator.toHandle().destroyForcibly()) {
                    System.out.println(
                            "PROCESS " + rank + ": Process forcibly destroyed correctly CoppeliaSim at port : " + port);
                } else {
                    System.out.println(
                            "PROCESS " + rank + ": Process did NOT forcibly destroy CoppeliaSim at port : " + port);
                }
                simulator.destroy();
                simulator.getErrorStream().close();
                simulator.getInputStream().close();
                simulator.getOutputStream().close();
                if (simulator != null) {
                    try {
                        System.out.println("PROCESS " + rank + ": Waiting for process to finish");
                        exitStatus = simulator.waitFor();
                        System.out.println("PROCESS " + rank + ": Process finished");
                        if (!SystemUtils.IS_OS_WINDOWS) {

                            // killUnixProcess(simulator);
                        }

                    } catch (InterruptedException ex) {
                        Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("PROCESS " + rank + ": CoppeliaSim finished with code: " + exitStatus);
            // if (!SystemUtils.IS_OS_WINDOWS && MPI.COMM_WORLD.Rank() == 0) {
            //// System.out.println(" (" + rank + ")Ps ux 2: ");
            //// getPsUX();
            // }

            try {
                Thread.sleep(2000); // wait for the coppeliaSimApi simulators
            } catch (InterruptedException ex) {
                Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        coppeliaSimApi.simxFinish(clientID);
        System.out.println("PROCESS " + rank + ": CoppeliaSim communicattion thread has finished ");
        simulator = null;
    }

    public long getUnixPID(Process process) throws Exception {

        long pid = process.pid();
        System.out.println(process.getClass().getName() + ", pid: " + pid);
        return pid;
        // if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
        // Class cl = process.getClass();
        // Field field = cl.getDeclaredField("pid");
        // field.setAccessible(true);
        // Object pidObject = field.get(process);
        // return (Integer) pidObject;
        // } else {
        // throw new IllegalArgumentException("Needs to be a UNIXProcess");
        // }
    }

    public void killUnixProcess(Process process) throws Exception {
        long pid = getUnixPID(process);
        System.out.println("PROCESS " + rank + ": PID of the CoppeliaSim is: " + pid);
        // return Runtime.getRuntime().exec("pkill -TERM -P " + pid).waitFor();
        // return Runtime.getRuntime().exec("kill -- -$(ps -o pgid= "+ pid +" | grep -o
        // '[0-9]*')").waitFor();

        // System.out.println(" (" + rank + ")Stopping CoppeliaSim at port : " + port);
        // if(process.toHandle().destroy())
        // {
        // System.out.println(" (" + rank + ")Process destroyed correctly CoppeliaSim at
        // port : " + port);
        // }else{
        if (process.toHandle().destroyForcibly()) {
            System.out.println(
                    "PROCESS " + rank + ": Process forcibly destroyed correctly CoppeliaSim at port : " + port);
        } else {
            System.out.println("PROCESS " + rank + ": Process did NOT forcibly destroy CoppeliaSim at port : " + port);
        }
        // }
    }

    public void pkillCoppeliaSim() {
        try {
            try {
                Runtime.getRuntime().exec("pkill coppeliasim").waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
            }
            Thread.sleep(200);
        } catch (IOException ex) {
            Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        ;
    }

    public void pkillJava() {
        try {
            try {
                Runtime.getRuntime().exec("pkill java").waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
            }
            Thread.sleep(200);
        } catch (IOException ex) {
            Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        ;
    }

    public int killCoppeliaSim() throws Exception {
        System.out.println("PROCESS " + rank + ": Stopping CoppeliaSim at port : " + port);
        return Runtime.getRuntime().exec("./removeCoppeliaSim.sh " + port).waitFor();
    }

    public int killall(String command) throws Exception {
        return Runtime.getRuntime().exec("killall " + command).waitFor();
    }

    public void getPsUX() {

        String str = "";
        try {

            List<String> processArguments = new ArrayList<String>();
            processArguments.add("ps");
            processArguments.add("ux");
            ProcessBuilder qq = new ProcessBuilder(processArguments);
            Process p = qq.start();
            p.waitFor();
            BufferedReader brStdOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader brStdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String strErr = "", strOut = "";
            System.out.println("\n");
            while (brStdOut.ready()) {
                strOut = "(" + rank + ")";
                strOut += brStdOut.readLine();
                System.out.println(strOut);
            }
            System.out.println("\n");
            while (brStdErr.ready()) {
                strErr = "(" + rank + ")";
                strErr += brStdErr.readLine();
                System.err.println(strOut);
            }
            // str = strOut + strErr;
            brStdOut.close();
            brStdErr.close();

        } catch (IOException ex) {
            Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(CoppeliaSimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getClientID() {
        return clientID;
    }

    public remoteApi getCoppeliaSimApi() {
        return coppeliaSimApi;
    }

    public void setGuiOn(boolean guiOn) {
        this.guiOn = guiOn;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public void setCoppeliaSimApi(remoteApi coppeliaSimApi) {
        this.coppeliaSimApi = coppeliaSimApi;
    }

    /**
     * Moves (and rotates) an object in CoppeliaSim. It uses world coordinates.*
     */
    public void moveObjectTo(String objectName, Vector3D posVec, Rotation rot) {
        if (clientID != -1) {
            IntW sensorHandle = new IntW(0);
            int err = coppeliaSimApi.simxGetObjectHandle(clientID, objectName, sensorHandle,
                    remoteApi.simx_opmode_blocking);
            if (err != remoteApi.simx_error_noerror) {
                System.err.println(objectName + " object not found in CoppeliaSim scene!");
            } else {

                // Set the rotation
                if (rot != null) {
                    // final FloatWA rotCoppelia = new FloatWA(4);
                    // rotCoppelia.getArray()[0] = (float) rot.getQ1();
                    // rotCoppelia.getArray()[1] = (float) rot.getQ2();
                    // rotCoppelia.getArray()[2] = (float) rot.getQ3();
                    // rotCoppelia.getArray()[3] = (float) rot.getQ0();
                    // err = coppeliaSimApi.simxSetObjectQuaternion(clientID,
                    // sensorHandle.getValue(),
                    // -1, rotCoppelia, remoteApi.simx_opmode_blocking);

                    // Trick to get the right eule angles
                    ModuleRotation modRot = new ModuleRotation(rot);
                    final FloatWA eulerAngles = new FloatWA(3);
                    eulerAngles.getArray()[0] = (float) modRot.getEulerAngles()[0];
                    eulerAngles.getArray()[1] = (float) modRot.getEulerAngles()[1];
                    eulerAngles.getArray()[2] = (float) modRot.getEulerAngles()[2];
                    err = coppeliaSimApi.simxSetObjectOrientation(clientID, sensorHandle.getValue(),
                            -1, eulerAngles, remoteApi.simx_opmode_blocking);

                    if (err != remoteApi.simx_error_noerror) {
                        System.err.println(objectName + "object not rotated! err=" + err);
                    }
                }

                // Set position
                if (posVec != null) {
                    FloatWA pos = new FloatWA(3);
                    pos.getArray()[0] = (float) posVec.getX();
                    pos.getArray()[1] = (float) posVec.getY();
                    pos.getArray()[2] = (float) posVec.getZ();
                    err = coppeliaSimApi.simxSetObjectPosition(clientID, sensorHandle.getValue(),
                            -1, pos, remoteApi.simx_opmode_blocking);
                    if (err != remoteApi.simx_error_noerror) {
                        System.err.println(objectName + "object not moved! err=" + err);
                    }
                }
            }
        }
    }

    public void recursiveConnectObjectTo(String childName, String parentName) {
        int childHandle = getObjectHandle(childName);
        int parentHandle = getObjectHandle(parentName);
        recursiveConnectObjectTo(childHandle, parentHandle);

    }

    public void recursiveConnectObjectTo(int childHandle, int parentHandle) {
        int dadOfChildHandle = getParentHandle(childHandle);
        connectObjectTo(childHandle, parentHandle);
        if (dadOfChildHandle != -1) {
            recursiveConnectObjectTo(dadOfChildHandle, childHandle);
        }
    }

    public void connectObjectTo(int childHandle, int parentHandle) {
        if (clientID != -1) {
            int err = err = coppeliaSimApi.simxSetObjectParent(clientID, childHandle,
                    parentHandle, true, remoteApi.simx_opmode_blocking);
            if (err != remoteApi.simx_error_noerror) {
                System.err.println(childHandle + "object was not moved to " + parentHandle + "! err=" + err);
            }
        }
    }

    public void connectObjectTo(String childName, String parentName) {
        if (clientID != -1) {
            int childHandle = getObjectHandle(childName);
            int parentHandle = getObjectHandle(parentName);
            connectObjectTo(childHandle, parentHandle);
        }
    }

    public void pauseSimulation() {
        int err = coppeliaSimApi.simxPauseSimulation(clientID, remoteApi.simx_opmode_blocking);
        if (err != remoteApi.simx_error_noerror) {
            System.err.println("The simulation was not paused! err=" + err);
        }
    }

    public void startSimulation() {
        int err = coppeliaSimApi.simxStartSimulation(clientID, remoteApi.simx_opmode_blocking);
        if (err != remoteApi.simx_error_noerror) {
            System.err.println("The simulation was not restarted! err=" + err);
        }
    }

    public void stopSimulation() {
        int err = coppeliaSimApi.simxStopSimulation(clientID, remoteApi.simx_opmode_blocking);
        if (err != remoteApi.simx_error_noerror) {
            System.err.println("The simulation was not stopped! err=" + err);
        }
    }

    public int getObjectHandle(String objectName) {
        IntW objectHandle = new IntW(0);
        int err = coppeliaSimApi.simxGetObjectHandle(clientID, objectName, objectHandle,
                remoteApi.simx_opmode_blocking);
        if (err != remoteApi.simx_error_noerror) {
            System.err.println(objectName + " object not found");
        }
        return objectHandle.getValue();
    }

    public int getParentHandle(String name) {
        return getParentHandle(getObjectHandle(name));
    }

    public int getParentHandle(int childObjectHandle) {
        IntW parentHandle = new IntW(0);
        int err = coppeliaSimApi.simxGetObjectParent(clientID, childObjectHandle, parentHandle,
                remoteApi.simx_opmode_blocking);
        if (err != remoteApi.simx_error_noerror) {
            System.err.println("Error when getting the parent handle! err=" + err);
        }
        return parentHandle.getValue();
    }

}

/*
 * Code taken from
 * http://www.javaworld.com/article/2071275/core-java/when-runtime-exec---won-t.
 * html
 */
class StreamGobbler extends Thread {

    InputStream is;
    String type;

    StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            for (;;) {
                while ((line = br.readLine()) != null) {
                    System.out.println(type + ">" + line);
                }
                try {
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                    Logger.getLogger(StreamGobbler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

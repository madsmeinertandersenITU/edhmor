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
package tests.coppeliasim;

import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.remoteApi;

/**
 *
 * @author fai
 */
public class ModulesCoppeliaSimTest {
    
    	public static void main(String[] args)
	{
		System.out.println("Program started");
		remoteApi coppeliaSimApi = new remoteApi();
		coppeliaSimApi.simxFinish(-1); // just in case, close all opened connections
		int clientID = coppeliaSimApi.simxStart("127.0.0.1",19997,true,true,5000,5);
		if (clientID!=-1)
		{
			System.out.println("Connected to remote API server");	

			// Now try to retrieve data in a blocking fashion (i.e. a service call):
			IntWA objectHandles = new IntWA(1);
			int ret=coppeliaSimApi.simxGetObjects(clientID,coppeliaSimApi.sim_handle_all,objectHandles,coppeliaSimApi.simx_opmode_oneshot_wait);
			if (ret==coppeliaSimApi.simx_return_ok)
				System.out.format("Number of objects in the scene: %d\n",objectHandles.getArray().length);
			else
				System.out.format("Remote API function call returned with error code: %d\n",ret);
				
			try
			{
				Thread.sleep(500);
			}
			catch(InterruptedException ex)
			{
				Thread.currentThread().interrupt();
			}
                        
                        //Add a parent model in the scene
                        String modelPathAndName = "models/edhmor/oldEdhmorModules/rotational.ttm";
                        IntW parentModuleHandle = new IntW(0);
                        //clientID,final String modelPathAndName, int options, IntW baseHandle, int operationMode
                        ret = coppeliaSimApi.simxLoadModel(clientID, modelPathAndName, 0, parentModuleHandle, remoteApi.simx_opmode_oneshot_wait);
                        
                        if (ret==remoteApi.simx_return_ok)
				System.out.format("Model loaded correctly: %d\n",parentModuleHandle.getValue());
			else
				System.out.format("Remote API function call returned with error code: %d\n",ret);

                        //Add a new model in the scene
                        IntW moduleHandle = new IntW(0);
                        //clientID,final String modelPathAndName, int options, IntW baseHandle, int operationMode
                        ret = coppeliaSimApi.simxLoadModel(clientID, modelPathAndName, 0, moduleHandle, coppeliaSimApi.simx_opmode_oneshot_wait);
                        
                        if (ret==coppeliaSimApi.simx_return_ok)
				System.out.format("Model loaded correctly: %d\n",moduleHandle.getValue());
			else
				System.out.format("Remote API function call returned with error code: %d\n",ret);
                        
                        
                        
                        
                        //Set the parent
                        //int simxSetObjectParent(int clientID,int objectHandle,int parentObject,boolean keepInPlace,int operationMode)
                        ret = coppeliaSimApi.simxSetObjectParent(clientID, moduleHandle.getValue(), parentModuleHandle.getValue()+3, true, coppeliaSimApi.simx_opmode_oneshot_wait);
                        if (ret==coppeliaSimApi.simx_return_ok)
				System.out.format("Parent model assinged correctly: %d\n",moduleHandle.getValue());
			else
				System.out.format("Remote API function call returned with error code: %d\n",ret);
	
                        //Move the model in the scene
                        //int simxSetObjectPosition(int clientID,int objectHandle, int relativeToObjectHandle, final FloatWA position, int operationMode)
                        FloatWA pos = new FloatWA(3);
                        pos.getArray()[0]=0.1f;
			pos.getArray()[1]=0.0f;
			pos.getArray()[2]=0.15f;
                        
                        ret = coppeliaSimApi.simxSetObjectPosition(clientID, moduleHandle.getValue(), -1, pos, coppeliaSimApi.simx_opmode_oneshot_wait);
                        if (ret==coppeliaSimApi.simx_return_ok)
				System.out.format("Model moved correctly: %d\n",moduleHandle.getValue());
			else
				System.out.format("Remote API function call returned with error code: %d\n",ret);
                        
                        
			// enable the synchronous mode on the client:
			coppeliaSimApi.simxSynchronous(clientID,true);

			// start the simulation:
			coppeliaSimApi.simxStartSimulation(clientID,coppeliaSimApi.simx_opmode_oneshot_wait);

			// Now step a few times:
			for (int i=0;i<300;i++)
			{
				//System.out.println("Press enter to step the simulation!");
				//String input=System.console().readLine();
				coppeliaSimApi.simxSynchronousTrigger(clientID);
			}

			// stop the simulation:
			coppeliaSimApi.simxStopSimulation(clientID,coppeliaSimApi.simx_opmode_oneshot_wait);
                        
                        
			// Before closing the connection to V-REP, make sure that the last command sent out had time to arrive. You can guarantee this with (for example):
			IntW pingTime = new IntW(0);
			coppeliaSimApi.simxGetPingTime(clientID,pingTime);

			// Now close the connection to V-REP:	
			coppeliaSimApi.simxFinish(clientID);
		}
		else
			System.out.println("Failed connecting to remote API server");
		System.out.println("Program ended");
	}
    
}

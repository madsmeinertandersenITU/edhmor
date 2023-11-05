package modules.control;

import java.util.Random;

import coppelia.remoteApi;
import modules.evaluation.CoppeliaSimCreateRobot;

public class SinusoidalControllerWNoise extends SinusoidalController{
    
    private double noiseStd;
    private boolean indivNoise = false;
    private Random randomGen;

    public SinusoidalControllerWNoise(remoteApi api, int clientID, CoppeliaSimCreateRobot robot,double noiseStd, boolean indivNoise) {
        super(api, clientID, robot);
        // TODO Auto-generated constructor stub
        this.noiseStd = noiseStd;
        this.indivNoise = indivNoise;
        randomGen = new Random();
        //System.out.println("Noise!!!");
    }
    
public boolean updateJoints(double time) {
    
        double noise = 0.0;
        
        //TODO: Remove the first module (as it is a base module without actuators)
    
        if(!indivNoise) {
            noise = (Math.PI/2)*(noiseStd*randomGen.nextGaussian());
        }
        
        coppeliaSimApi.simxPauseCommunication(clientID,true);
        
        for (int module = 0; module < moduleHandlers.size(); module++) {
            //TODO: this iterates for each module but we should also iterate for 
            //each actuator in the module. For now, just suppose that all the
            //modules have only one dof.
            
            if(indivNoise) {
                noise = (Math.PI/2)*(noiseStd*randomGen.nextGaussian());
            }
            
            float targetPosition;
            if(usePhaseControl && !useAngularFreqControl && !useAmplitudeControl){
                targetPosition= (float) (maxAmplitude[module] * Math.sin(maxAngFreq[module] * time + phaseControl[module] / 180. * Math.PI) + noise);
            }else{
                double amplitude = maxAmplitude[module] * amplitudeControl[module];
                double angularFreq = maxAngFreq[module] * andularFreqControl[module];
                targetPosition= (float) (amplitude * Math.sin(angularFreq * time + phaseControl[module] / 180. * Math.PI) + noise);
            }
            
            int ret = coppeliaSimApi.simxSetJointTargetPosition(clientID, moduleHandlers.get(module) + 2, targetPosition, remoteApi.simx_opmode_oneshot);
            if (ret == remoteApi.simx_return_ok || ret == remoteApi.simx_return_novalue_flag) {
                //System.out.format("Target position set: %d to %f\n", joint + 2, targetPosition);
            } else {
                System.out.format("updateJoints Function: Remote API function call returned with error code: %d\n", ret);
                return false;
            }
        }
        
        coppeliaSimApi.simxPauseCommunication(clientID,false);
        return true;
    }

}

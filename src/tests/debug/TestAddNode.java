/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests.debug;

import es.udc.gii.common.eaf.util.EAFRandom;
import modules.evaluation.overlapping.BoundingBoxCollisionDetector;
import modules.individual.TreeIndividual;
import modules.jeaf.operation.MutationOperation;
import modules.jeaf.operation.decrease.DeleteAllNodes;
import modules.jeaf.operation.grow.AddNode;
import modules.jeaf.operation.ShakingControl;
import modules.jeaf.operation.morphological.ShakingModule;

/**
 *
 * @author anfv
 */
public class TestAddNode {

    private static final int eval = 1000;

    public static void main(String[] args) {

        boolean collisionIni, collisionWithNode, collisionRepaired;
        BoundingBoxCollisionDetector collisionDetector = new BoundingBoxCollisionDetector();
        
        for (int i = 0; i < eval; i++) {

            String str, indIni, indWithNode, indRepaired; 
            EAFRandom.init();
            TreeIndividual randomTree = new TreeIndividual();
            randomTree.init(141);
            randomTree.generate();
            indIni = randomTree.toString();
            
            collisionDetector.loadTree(randomTree);
            collisionIni = collisionDetector.isFeasible();
            str = "Random tree, collision: " + !collisionIni;
            
            MutationOperation op = new DeleteAllNodes();
            op.run(randomTree);
            indWithNode = randomTree.toString();
            
            collisionDetector.loadTree(randomTree);
            collisionWithNode = collisionDetector.isFeasible();
            str += "; Random tree + AddNode, collision: " + !collisionWithNode;
            
            if(!collisionWithNode)
                System.out.println("op: " + op.isWorking());
            
            op.repair(randomTree);
            indRepaired = randomTree.toString();
                    
            collisionDetector.loadTree(randomTree);
            collisionRepaired = collisionDetector.isFeasible();
            str += "; Random tree + repaired, collision: " + !collisionRepaired;
            
            if(!collisionWithNode ){
                System.out.println("indIni: " + indIni);
                System.out.println("indWithNode: " + indWithNode);
                System.out.println("indRepaired: " + indRepaired);
            }
            
            //System.out.println(str);
        }
    }

}

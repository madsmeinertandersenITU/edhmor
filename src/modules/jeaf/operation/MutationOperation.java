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
package modules.jeaf.operation;

import es.udc.gii.common.eaf.config.Configurable;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.individual.Node;
import modules.individual.TreeIndividual;

/**
 *
 * @author fai
 */
public abstract class MutationOperation implements Configurable{

    //boolean variable to indicate if the operation is working
    private boolean isWorking = true;

    /**Function that run the mutation (the operation). It has to be implemented 
     in all the classes that inheritage from MutationOperation.
     * @param tree     the tree individual where the operation will be applied*/
    public abstract void run(TreeIndividual tree);

    /**Function that repairs the individual. It has to be implemented in all the
     * classes that inheritage from MutationOperation. Depending of the 
     * implementation it can save the mutated individual or the original 
     * individual (with out this mutation)
     * @param tree      the tree individual to repair
     */
    public abstract void repair(TreeIndividual tree);

    public void fakeRepair(TreeIndividual tree){
        //The fitness has not improved enough. We dont increase the 
        //fitnessContribution and we unset the boolean variable 
        //isActiveContribution
     
        tree.getRootNode().addFitnessContributionAndResetOperationalActive(0);
    }


    @Override
    public abstract String toString();
    
    /**It copies the root Node of the current individual and set it as "parent" 
     * individual of this mutation. 
     * It also copies the fitness of the original individual as the fitness of 
     * the "parent". The parent individual is used in case that the repair 
     * function decides to undo the mutation.
     * @param tree the original individual*/
    protected void setFatherIndividual(TreeIndividual tree){
        Node rootN;
        try {
            tree.setFatherRootNode(null);
            rootN = tree.getRootNode().clone();
            tree.setFatherRootNode(rootN);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(MutationOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        tree.setFatherFitness(tree.getFitness());
    }

    /**Removes the parent individual and resets its fitness.
     * @param tree    individual to remove its father individual 
     */
    protected void removeFatherIndividual(TreeIndividual tree){
        tree.setFatherRootNode(null);
        tree.setFatherFitness(Double.NEGATIVE_INFINITY);
    }

    public boolean isWorking() {
        return isWorking;
    }

    protected void setIsWorking(boolean b) {
        this.isWorking = b;
    }

    public abstract boolean isMandatory();



    

}

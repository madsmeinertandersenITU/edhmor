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
package modules.jeaf.operation.decrease;

import es.udc.gii.common.eaf.algorithm.fitness.comparator.FitnessComparator;
import es.udc.gii.common.eaf.algorithm.fitness.comparator.MaximizingFitnessComparator;
import es.udc.gii.common.eaf.algorithm.population.Individual;
import java.util.ArrayList;
import java.util.List;
import modules.jeaf.comparator.MaximizingFitnessMinimizingNodesComparator;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.util.SimulationConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class DeleteAllNodes extends DecreaseMutationOperation {

    private boolean useBestFitness = false;

    @Override
    public void run(TreeIndividual arbol) {

        //Hay que poner el arbol como "padre" antes de modificarlo
        this.setFatherIndividual(arbol);

        this.setIsWorking(true);

        
    }

    @Override
    public void repair(TreeIndividual arbol) {

        double diferenciaFitness = arbol.getFitness() - arbol.getFatherFitness();

        FitnessComparator comparator = null;
        if(this.useBestFitness)
            comparator = new MaximizingFitnessComparator();
        else
            comparator = new MaximizingFitnessMinimizingNodesComparator();

        TreeIndividual fatherTree = arbol.clone();
        fatherTree.setRootNode(fatherTree.getFatherRootNode());
        fatherTree.setFitness(fatherTree.getFatherFitness());

        if(comparator.compare(arbol, fatherTree) < 0){
            arbol.getRootNode().addFitnessContributionAndResetOperationalActive(0);
        }else{
            arbol.setRootNode(arbol.getFatherRootNode());
            arbol.setFitness(arbol.getFatherFitness());

            //Aumentamos el fitnessContribution
            arbol.getRootNode().addFitnessContributionAndResetOperationalActive(0);

            //actualizamos al individuo
            arbol.modifyChromosome();
        }
    }

    @Override
    public boolean isMandatory() {
        return false;
    }

    @Override
    public String toString() {
        String str = "DeleteAllNodes";
        if(this.isWorking())
            str += "   (isWorking)";
        else
            str += "   (NOT Working)";
        return str;
    }

    public void configure(Configuration conf) {
        
        this.useBestFitness = conf.containsKey("useBestFitness");

    }

    public List<Individual> generateCandidates(Individual ind) {

        TreeIndividual arbol = (TreeIndividual) ind;

        List<Individual> candidates = new ArrayList<Individual>();

        for (int i = 1; i < arbol.getRootNode().getNumberModulesBranch(); i++) {
            TreeIndividual arbolClone = arbol.clone();
            Node toRemove = null;
            List<Node> nodes = arbolClone.getListNode();
            for (Node nod : nodes) {
                if (nod.getConstructionOrder() == i) {
                    toRemove = nod;
                }
            }

            toRemove.getDad().eliminateChild(toRemove);
            toRemove.eliminateBranch();

            arbolClone.modifyChromosome();
            //Only add to the list if there are enough modules
            if(arbolClone.getListNode().size() >= SimulationConfiguration.getNMinModulesIni())
                candidates.add(arbolClone); 
        }
        return candidates;

    }
}

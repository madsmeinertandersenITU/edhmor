package modules.jeaf.log.parallel;

import java.util.Observable;

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.parallel.ParallelEvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.population.Individual;
import es.udc.gii.common.eaf.algorithm.productTrader.IndividualsProductTrader;
import es.udc.gii.common.eaf.algorithm.productTrader.specification.BestIndividualSpecification;
import es.udc.gii.common.eaf.log.parallel.ParallelLogTool;
import modules.evaluation.CalculateModulePositions;
import modules.evaluation.staticFeatures.SymmetryFeature;
import modules.evaluation.staticFeatures.XSymmetryEvaluator;
import modules.evaluation.staticFeatures.YSymmetryEvaluator;
import modules.individual.TreeIndividual;
import modules.jeaf.application.edhmor.RoboticObjetiveFunction;

public class RobotBestDescriptorsLogTool extends ParallelLogTool{
	
	@Override
    public String getLogID() {
        return "ParallelRobotBestDescriptorsLogTool";
    }
	
	@Override
    public void update(Observable o, Object arg) {
		
		ParallelEvolutionaryAlgorithm pea = (ParallelEvolutionaryAlgorithm) o;
		
		if (pea.getCurrentObservable() instanceof EvolutionaryAlgorithm) {
            super.update(o, arg);
            EvolutionaryAlgorithm algorithm = (EvolutionaryAlgorithm) pea.getCurrentObservable();
            
            BestIndividualSpecification bestSpec =
                    new BestIndividualSpecification();
            Individual best;
            
            if (algorithm.getState() == EvolutionaryAlgorithm.REPLACE_STATE && arg == null) {
                best = IndividualsProductTrader.get(bestSpec,
                        algorithm.getPopulation().getIndividuals(), 1, pea.getComparator()).get(0);
                
                TreeIndividual bestTree = (TreeIndividual) best;
                CalculateModulePositions features = new CalculateModulePositions(bestTree.getChromosomeAt(0));
                
              //Symmetry
                SymmetryFeature xSymmetryFeature = new XSymmetryEvaluator();
                SymmetryFeature ySymmetryFeature = new YSymmetryEvaluator();
      	        double xSymmetryValue, ySymmetryValue;
      	        ySymmetryValue = ySymmetryFeature.getSymmetryMeasurement(bestTree);
                xSymmetryValue = xSymmetryFeature.getSymmetryMeasurement(bestTree);
                
                super.getLog().println(
                        algorithm.getGenerations() + " - " +
                        best.getFitness() + " - " +
                        + features.getnModules() + " - "
                        + features.getLimbDescriptor()+ " - "
                        + features.getCoverage() + " - "
                        + features.getXYProportion() + " - "
                        + features.getZProportion() + " - "
                        + xSymmetryValue + " - "
                        + ySymmetryValue + " - "
                        + best.getObjectives().get(RoboticObjetiveFunction.DFEATURES_BALANCE)
                        );

                
            }
		}
	}

}

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

package modules.evaluation.overlapping;

import modules.ModuleSet;
import modules.ModuleSetFactory;
import modules.evaluation.CalculateModulePositions;
import modules.individual.SensorTree;
import modules.individual.TreeIndividual;

/**
 * CollisionDetector.java
 * Created on 16/02/2016
 * 
 * @author Andres Faiña <anfv at itu.dk>
 */
public abstract class CollisionDetector {

    protected ModuleSet moduleSet;
    protected CalculateModulePositions robotFeatures;

    public abstract boolean isFeasible();

    public void loadTree(TreeIndividual t) {
        // Load the module set
        moduleSet = ModuleSetFactory.getModulesSet();
        robotFeatures = new CalculateModulePositions(t.getChromosomeAt(0));
    }

    public void loadSensorTree(SensorTree t) {
        // Load the module set
        moduleSet = ModuleSetFactory.getModulesSet();
        robotFeatures = new CalculateModulePositions(t.getChromosomeAt(0));
    }
}

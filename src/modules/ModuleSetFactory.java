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

import modules.baseLength.Emerge18ModuleSetBaseLength;
import modules.baseLength.EmergeModuleSetBaseLength;
import modules.numberOfFaces.RodrigoModuleSet;
import modules.numberOfFaces.RodrigoModuleSet_1;
import modules.numberOfFaces.RodrigoModuleSet_1_2;
import modules.numberOfFaces.RodrigoModuleSet_1_2_3;
import modules.numberOfFaces.RodrigoModuleSet_2;
import modules.numberOfFaces.RodrigoModuleSet_2_3;
import modules.util.SimulationConfiguration;

/**
 * ModuleSetFactory returns the current set of modules to use.
 * <p>
 * This static object can be call to obtain the current module set. It loads the
 * module set when the program starts. It stores a copy of the set in this
 * object, which is the object returned when getModulesSet is called.
 * 
 * 
 * ModuleSetFactory.java Created on 18/10/2015
 *
 * @author Andres Faiña <anfv at itu.dk>
 */
public class ModuleSetFactory {

    /**
     * This is the current module set whis is being used.
     */
    static ModuleSet moduleSet = loadModuleSet();

    /**
     * reloadModuleSet reloads the module set if this has been changed. As an
     * example, this function is called in the GUI to change the set as required.
     */
    public static void reloadModuleSet() {
        moduleSet = loadModuleSet();
    }

    /**
     * loadModuleSet loads the module set. It reads the string stored in
     * SimulationConfiguration and creates a new module set according to this
     * value.
     */
    private static ModuleSet loadModuleSet() {
        ModuleSet set = null;

        if (SimulationConfiguration.getModuleSet().contentEquals("EmergeModules"))
            set = new EmergeModuleSet();
        if (SimulationConfiguration.getModuleSet().contentEquals("Emerge18Modules"))
            set = new Emerge18ModuleSet();
        if (SimulationConfiguration.getModuleSet().contentEquals("RodrigoModules"))
            set = new RodrigoModuleSet();
        if (SimulationConfiguration.getModuleSet().contentEquals("EmergeAndCuboidBaseModules"))
            set = new EmergeAndCuboidBaseModuleSet();

        if (SimulationConfiguration.getModuleSet().contentEquals("Emerge18AndCuboidBaseModules"))
            set = new Emerge18AndCuboidBaseModuleSet();

        if (SimulationConfiguration.getModuleSet().contentEquals("RealEdhmorModules"))
            set = new RealEdhmorModuleSet();
        if (SimulationConfiguration.getModuleSet().contentEquals("OldEdhmorModules"))
            set = new OldEdhmorModuleSet();
        if (SimulationConfiguration.getModuleSet().contentEquals("TestModules"))
            set = new TestModuleSet();
        if (SimulationConfiguration.getModuleSet().contentEquals("RodrigoModules_1_2_3"))
            set = new RodrigoModuleSet_1_2_3();
        if (SimulationConfiguration.getModuleSet().contentEquals("RodrigoModules_2_3"))
            set = new RodrigoModuleSet_2_3();
        if (SimulationConfiguration.getModuleSet().contentEquals("RodrigoModules_1_2"))
            set = new RodrigoModuleSet_1_2();
        if (SimulationConfiguration.getModuleSet().contentEquals("RodrigoModules_1"))
            set = new RodrigoModuleSet_1();
        if (SimulationConfiguration.getModuleSet().contentEquals("RodrigoModules_2"))
            set = new RodrigoModuleSet_2();
        if (SimulationConfiguration.getModuleSet().contentEquals("EmergeModules1-25TimesLength"))
            set = new EmergeModuleSetBaseLength(1.25);
        if (SimulationConfiguration.getModuleSet().contentEquals("EmergeModules1-5TimesLength"))
            set = new EmergeModuleSetBaseLength(1.5);
        if (SimulationConfiguration.getModuleSet().contentEquals("EmergeModules1-75TimesLength"))
            set = new EmergeModuleSetBaseLength(1.75);
        if (SimulationConfiguration.getModuleSet().contentEquals("EmergeModules2TimesLength"))
            set = new EmergeModuleSetBaseLength(2);
        if (SimulationConfiguration.getModuleSet().contentEquals("EmergeModules4TimesLength"))
            set = new EmergeModuleSetBaseLength(4);
        if (SimulationConfiguration.getModuleSet().contentEquals("EmergeModules8TimesLength"))
            set = new EmergeModuleSetBaseLength(8);
        if (SimulationConfiguration.getModuleSet().contentEquals("EmergeAndFlatBaseModules"))
            set = new EmergeFlatBaseModuleSet();

        if (SimulationConfiguration.getModuleSet().contentEquals("Emerge18Modules1-25TimesLength"))
            set = new Emerge18ModuleSetBaseLength(1.25);
        if (SimulationConfiguration.getModuleSet().contentEquals("Emerge18Modules1-5TimesLength"))
            set = new Emerge18ModuleSetBaseLength(1.5);
        if (SimulationConfiguration.getModuleSet().contentEquals("Emerge18Modules1-75TimesLength"))
            set = new Emerge18ModuleSetBaseLength(1.75);
        if (SimulationConfiguration.getModuleSet().contentEquals("Emerge18Modules2TimesLength"))
            set = new Emerge18ModuleSetBaseLength(2);
        if (SimulationConfiguration.getModuleSet().contentEquals("Emerge18Modules4TimesLength"))
            set = new Emerge18ModuleSetBaseLength(4);
        if (SimulationConfiguration.getModuleSet().contentEquals("Emerge18Modules8TimesLength"))
            set = new Emerge18ModuleSetBaseLength(8);
        if (SimulationConfiguration.getModuleSet().contentEquals("Emerge18AndFlatBaseModules"))
            set = new Emerge18FlatBaseModuleSet();
        if (SimulationConfiguration.getModuleSet().contentEquals("EmergeProximitySensorModules"))
            set = new EmergeProximitySensorModuleSet();

        if (SimulationConfiguration.getModuleSet().contentEquals("AtronModules"))
            set = new AtronModuleSet();
        if (SimulationConfiguration.getModuleSet().contentEquals("PolyBotModules"))
            set = new PolyBotModuleSet();
        if (SimulationConfiguration.getModuleSet().contentEquals("BAtronModules"))
            set = new BAtronModuleSet();
        if (SimulationConfiguration.getModuleSet().contentEquals("Y1Modules"))
            set = new Y1ModuleSet();

        if (set == null) {
            System.out.println(
                    "Failed ModulesSet initialization. Check the ModuleSet property in the configuration file.");
            System.exit(-1);
        }

        return set;

    }

    /**
     * getModulesSet returns the current module set.
     * 
     * @return the current module set
     */
    public final static ModuleSet getModulesSet() {
        return moduleSet;
    }

}

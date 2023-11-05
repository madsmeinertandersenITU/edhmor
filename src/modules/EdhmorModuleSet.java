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

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * EdhmorModulesSet.java
 * Created on 18/10/2015
 * @author Andres Faiña <anfv  at itu.dk>
 */
public abstract class EdhmorModuleSet extends ModuleSet{

    public EdhmorModuleSet(int modulesTypeNumber, int maxFaceNumberInOneModule, int maxOrientations) {
        super(modulesTypeNumber, maxFaceNumberInOneModule, maxOrientations);
    }
    
    
    @Override
    public int getModulesBaseFacesNumber(int type) {
        return modulesBaseFacesNumber[type];
    }

    @Override
    public int getModulesFacesNumber(int type) {
        return modulesFacesNumber[type];
    }

    @Override
    public int getModuleOrientations(int type) {
        return moduleOrientations[type];
    }

    @Override
    public Vector3D getNormalFaceVector(int type, int face) {
        return new Vector3D(normalFaceVector[type][face].getX(),
        normalFaceVector[type][face].getY(),
        normalFaceVector[type][face].getZ());
    }

    @Override
    public Vector3D getOriginFaceVector(int type, int face) {
        return new Vector3D(originFaceVector[type][face].getX(),
        originFaceVector[type][face].getY(),
        originFaceVector[type][face].getZ());
    }


}

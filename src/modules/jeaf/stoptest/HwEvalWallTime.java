/*
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2021 Andres Faiña <anfv at itu.dk> (ITU)
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

package modules.jeaf.stoptest;

import modules.util.SimulationConfiguration;

/**
 * HwEvalWallTime created on Nov 8, 2021
 * 
 * @author Andres Faiña <anfv at itu.dk>
 */
public class HwEvalWallTime {
    
    private static double elapsedWallTime = 0; //Keeps time of the elapsed time (assembly robots + evaluation + dissasembly)
    
    public static double getElapsedWallTime() {
        return elapsedWallTime;
    }

    public static void setElapsedWallTime(double time) {
        HwEvalWallTime.elapsedWallTime = time;
    }
    
    public static void increaseElapsedWallTime(double time) {
        HwEvalWallTime.elapsedWallTime += time;
    }

}

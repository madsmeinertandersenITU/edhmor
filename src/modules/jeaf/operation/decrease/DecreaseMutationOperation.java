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

import modules.jeaf.operation.MutationOperation;
import modules.individual.Node;

/**
 *
 * @author fai
 */
public abstract class DecreaseMutationOperation extends MutationOperation {

    /**
     * Function that removes a branch of the individual
     */
    protected void removeBranch(Node toRemove) {

        //Get the dad of the module
        Node dad = toRemove.getDad();

        //Call to eliminate the child at the dad´s module 
        if (!dad.eliminateChild(toRemove)) {
            System.err.println("Node not eliminated");
        }

        //Delete all the references to the child to remove
        toRemove.eliminateBranch();
    }
}

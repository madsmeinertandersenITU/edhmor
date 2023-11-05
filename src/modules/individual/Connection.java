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
package modules.individual;

/**
 *
 * @author fai
 */
public class Connection implements Cloneable{
    
    private int dadFace, childrenOrientation;

    public Connection(Node dad, Node children) {
        dadFace= dad.setFaceParent();
        childrenOrientation=children.setChildrenOrientation();
    }

    public Connection(Node dad, Node children, int dadFace, int orientation) {
        this.dadFace= dad.setFaceParent(dadFace);
        this.childrenOrientation=children.setChildrenOrientation(orientation);
    }

    public int getChildrenOrientation() {
        return childrenOrientation;
    }

    public int getDadFace() {
        return dadFace;
    }

    public void setChildrenOrientation(int childrenFace) {
        this.childrenOrientation = childrenFace;
    }

    public void setDadFace(int dadFace) {
        this.dadFace = dadFace;
    }

    
    
    @Override
    public Connection clone() throws CloneNotSupportedException {
        Connection clone = (Connection) super.clone();
        return clone;
    }
    
    
}

/*
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2022 Andres Faiña <anfv at itu.dk> (ITU)
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

package apriltag;

/**
 * AprilTagFamily created on Nov 25, 2022
 * 
 * @author Andres Faiña <anfv at itu.dk>
 */
public class AprilTagFamily {
    public static final int TAG_36h11 = 0;      //< AprilTag 36h11 pattern (recommended)
    public static final int TAG_36h10 = 1;      //< DEPRECATED
    public static final int TAG_36ARTOOLKIT = 2;//< DEPRECATED AND WILL NOT DETECT ARTOOLKIT TAGS
    public static final int TAG_25h9 = 3;       //< AprilTag 25h9 pattern
    public static final int TAG_25h7 = 4;       //< DEPRECATED AND POOR DETECTION PERFORMANCE
    public static final int TAG_16h5 = 5;       //< AprilTag 16h5 pattern
    public static final int TAG_CIRCLE21h7 = 6; //< AprilTag Circle21h7 pattern
    public static final int TAG_CIRCLE49h12 = 7;//< AprilTag Circle49h12 pattern
    public static final int TAG_CUSTOM48h12 = 8;//< AprilTag Custom48h12 pattern
    public static final int TAG_STANDARD41h12 = 9;//< AprilTag Standard41h12 pattern
    public static final int TAG_STANDARD52h13 = 10;//< AprilTag Standard52h13 pattern
}

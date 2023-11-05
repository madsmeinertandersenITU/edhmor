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

package modules.util;

import java.io.File;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * NewClass created on Sep 26, 2022
 * 
 * @author Andres Faiña <anfv at itu.dk>
 * 
 * If not used this trick, it adds line breaks in the xml file when saving it.
 * Taken from: https://stackoverflow.com/questions/5761610/format-xml-output-with-apache-commons-configuration-xmlconfiguration/16211353#16211353
 */
public class ExtendedXMLConfiguration extends XMLConfiguration
{
    public ExtendedXMLConfiguration(String file) throws ConfigurationException
    {
        super(file);
    }

    @Override
    protected Transformer createTransformer() throws TransformerException {
        Transformer transformer = super.createTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        return transformer;
    }
}
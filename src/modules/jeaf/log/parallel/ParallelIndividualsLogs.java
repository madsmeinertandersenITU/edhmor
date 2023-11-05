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
package modules.jeaf.log.parallel;

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.log.LogPattern;
import es.udc.gii.common.eaf.log.parallel.ParallelLogTool;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Observable;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class ParallelIndividualsLogs extends ParallelLogTool{

        private PrintStream[] logs;
    private int nIndividuals;
    private String folder =
            System.getProperty("user.dir") + File.separatorChar + "OF" + File.separatorChar;

    private boolean doCreateFile = true;


    public PrintStream getLog(int c) {

        return this.logs[c];

    }

    @Override
    public void setFile(String fileName) {
        this.folder = fileName;
    }

    @Override
    public void configure(Configuration conf) {
        if (conf.containsKey("Folder")) {
            this.folder = conf.getString("Folder");
        }
        if (conf.containsKey("Name")) {
            this.name = conf.getString("Name");
        }
    }

    private void createFiles(String folder, String file_name) {

        File new_file;
        File new_folder;

        for (int i = 0; i < this.nIndividuals; i++) {
            try {
                if (!folder.endsWith(String.valueOf(File.separatorChar))) {
                    folder += File.separatorChar;
                }
                new_folder = new File(folder);
                new_folder.mkdirs();
                new_file = new File(folder + i + file_name);

                this.logs[i] = new PrintStream(new_file, "UTF-8");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    public void update(Observable o, Object arg) {

        EvolutionaryAlgorithm algorithm = (EvolutionaryAlgorithm) o;
        String folder_name, file_name;

        this.nIndividuals = algorithm.getPopulation().getSize();
//        if (algorithm.getState() == EvolutionaryAlgorithm.INIT_STATE) {
        if (doCreateFile) {
            doCreateFile = false;
            this.oldFolder = this.folder;
            folder_name = LogPattern.replace(this.folder, algorithm, this);
            this.oldName = this.name;
            file_name = LogPattern.replace(this.name + this.fileExtension, algorithm, this);

            this.logs = new PrintStream[this.nIndividuals];
            this.createFiles(folder_name, file_name);
        }

        if (algorithm.getState() == EvolutionaryAlgorithm.CLOSE_LOGS_STATE) {
            doCreateFile = true;
            this.folder = this.oldFolder;
            this.name = this.oldName;
            for (int i = 0; i < this.nIndividuals; i++)
                this.logs[i].close();
        }

//        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        for (int i = 0; i < this.nIndividuals; i++)
                this.logs[i].close();

    }
    
    @Override
    public String getLogID() {
        return "parallelindividualslogs";
    }

}

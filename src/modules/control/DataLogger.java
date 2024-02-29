package modules.control;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataLogger {
    private BufferedWriter writer;

    public DataLogger(String filename, String columnNames) {
        try {
            writer = new BufferedWriter(new FileWriter(filename));
            System.out.println(filename);
            writer.write(columnNames + "\n");
            System.out.println(columnNames);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logData(long currentTime, float jointPosition, float proximitySensorValue) {
        try {
            System.out.println("Debug: currentTime=" + currentTime + ", jointPosition=" + jointPosition
                    + ", proximitySensorValue=" + proximitySensorValue);

            // Format the string with periods as decimal separators and no leading zeros
            String formattedValue = String.format("%.2f", jointPosition);

            // Replace commas with periods before parsing
            formattedValue = formattedValue.replace(',', '.');

            // Convert the formatted string back to float
            float formattedFloatValue = Float.parseFloat(formattedValue);
            String formattedString = String
                    .format("" + currentTime + "," + "" + formattedFloatValue + "," + "" + proximitySensorValue + "");

            // Write the formatted string to the file
            writer.write(formattedString + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logDetectionData(long currentTime, String sensorValue) {
        try {
            // Write the formatted string to the file
            writer.write(currentTime + "," + sensorValue + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logPosition(float topPosition, float bottomPosition) {
        try {
            // Write the formatted string to the file
            writer.write(topPosition + "," + bottomPosition + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logDistance(long currentTime, float distance) {
        try { // Format the string with periods as decimal separators and no leading zeros
            String formattedValue = String.format("%.2f", distance);

            // Replace commas with periods before parsing
            formattedValue = formattedValue.replace(',', '.');

            // Convert the formatted string back to float
            float formattedFloatValue = Float.parseFloat(formattedValue);
            String formattedString = String
                    .format("" + currentTime + "," + "" + formattedFloatValue);

            // Write the formatted string to the file
            writer.write(formattedString + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
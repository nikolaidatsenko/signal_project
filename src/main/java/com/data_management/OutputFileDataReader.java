package com.data_management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class OutputFileDataReader implements DataReader {
    private String outputDir;

    public OutputFileDataReader(String outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        String filePath = outputDir.substring(outputDir.indexOf(':') + 1); // Extracting the file path from the argument
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine();

        while (line != null) {// go over each line of the input dir until it is empty

            String[] recordParts = line.split(","); // split the line into 4 parts, each containing a label and one datapoint

            // each datapoint is being accessed and stored
            int patientId = Integer.parseInt(recordParts[0].split(": ")[1].trim());
            long timestamp = Long.parseLong(recordParts[1].split(": ")[1].trim());
            String type = recordParts[2].split(": ")[1].trim();
            double measurementValue = Double.parseDouble(recordParts[3].split(": ")[1].trim());

            // Add patient directly without checking existence as that is being done in addPatientData
            dataStorage.addPatientData(patientId, measurementValue, type, timestamp);

            line = reader.readLine();
        }
        reader.close();
    }

}

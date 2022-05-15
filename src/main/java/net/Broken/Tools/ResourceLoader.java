package net.Broken.Tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;


public class ResourceLoader {

    private Logger logger = LogManager.getLogger();

    /**
     * Get file contents as string for resource folder
     *
     * @param fileName Requested file
     * @return File contents as string
     * @throws FileNotFoundException
     */
    public String getFile(String fileName) throws FileNotFoundException {

        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream file = classLoader.getResourceAsStream(fileName);
        if (file == null)
            throw new FileNotFoundException();

        try (Scanner scanner = new Scanner(file, "UTF-8")) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        }

        return result.toString();

    }

}

package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Cat extends ShellProgram
{
    @Override
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream output) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(output);
        if (args.length == 0) {                                                        // if there is no argument, cant cat nothing
            throw new RuntimeException("cat: missing arguments");
        } else {
            for (String arg : args) {                                                // for each file specified in the arguments 
                Charset encoding = StandardCharsets.UTF_8;                              // print it using UTF 8 
                File currFile = new File(currentDirectory + File.separator + arg);      // gets the absolute path of the file
                if (currFile.exists()) {                                                // checks if it exists 
                    Path filePath = Paths.get(currentDirectory + File.separator + arg); // gets a path object from the filepath 
                    try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) { // tries offering a buffered reader on the file 
                        String line = null;                                             // initialises the line variable 
                        while ((line = reader.readLine()) != null) {                    // for each line that isnt empty in the file 
                            str_to_bytes.write(String.valueOf(line));                         // print the contents
                            str_to_bytes.write(System.getProperty("line.separator"));         // necessary
                            str_to_bytes.flush();
                        }
                    } catch (IOException e) {                                           //
                        throw new RuntimeException("cat: cannot open " + arg);          //
                    }
                } else {
                    throw new RuntimeException("cat: file does not exist");             //
                }
            }
        }
    }
}

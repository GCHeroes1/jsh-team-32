package uk.ac.ucl.jsh.shellprorgams;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Pipe extends ShellProgram
{
    public void execute(String[] args)
    {
        // so we need this to check for this at the end of any other instruction 
        // Pipe is a left-associative operator that can be used to bind a set of call commands into a 
        // chain. Each pipe operator binds the output of the left part to the input of the right part, 
        // then evaluates these parts concurrently. If an exception occurred in any of these parts, 
        // the execution of the other parts must be terminated 
        // <pipe> ::= <call> "|" <call> |
        //            <pipe> "|" <call
    }
}
    /*@Override
    public void execute(String[] args)
    {
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
                            writer.write(String.valueOf(line));                         // print the contents 
                            writer.write(System.getProperty("line.separator"));         // necessary 
                            writer.flush();
                        }
                    } catch (IOException e) {                                           //
                        throw new RuntimeException("cat: cannot open " + arg);          //
                    }
                } else {
                    throw new RuntimeException("cat: file does not exist");             //
                }
            }
        }
    }*/


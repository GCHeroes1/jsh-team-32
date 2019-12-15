 package uk.ac.ucl.jsh.shellprorgams;

import uk.ac.ucl.jsh.Jsh;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Cd extends ShellProgram
{
    @Override
    public void execute(String[] args, OutputStream output) throws IOException
    {
        ArrayList<String> appArgs = new ArrayList<>();
        Collections.addAll(appArgs, args);
        if (appArgs.isEmpty()) {                                                        // throw an exception if they dont provide a directory to switch to
            throw new RuntimeException("cd: missing argument");
        } else if (appArgs.size() > 1) {                                                // throws exception if you give too much stuff to cd
            throw new RuntimeException("cd: too many arguments");
        }
        String dirString = appArgs.get(0);                                              // gets the string representation of the path to switch to
        File dir = new File(currentDirectory, dirString);                               // gets a file system node
        if (!dir.exists() || !dir.isDirectory()) {                                      // checks if the file exists and is a directory
            throw new RuntimeException("cd: " + dirString + " is not an existing directory"); // throw exception if it cant find the path
        }
        currentDirectory = dir.getCanonicalPath();                                      // fully qualified path - absolute directory - way back up to root
    }
}

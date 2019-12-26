 package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Cd extends ShellProgram
{
    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        ArrayList<String> appArgs = new ArrayList<>();
        Collections.addAll(appArgs, args);
        if (appArgs.isEmpty()) {                                                        // throw an exception if they dont provide a directory to switch to
            throw new RuntimeException("cd: missing argument");
        } else if (appArgs.size() > 1) {                                                // throws exception if you give too much stuff to cd
            throw new RuntimeException("cd: too many arguments");
        }
        String dirString = appArgs.get(0);                                              // gets the string representation of the path to switch to
        File dir = new File(dirString);
        if(!dir.isAbsolute())
        {
            dir = new File(currentDirectory, dirString);
        }
        if (!dir.exists() || !dir.isDirectory()) {                                      // checks if the file exists and is a directory
            throw new RuntimeException("cd: " + dirString + " is not an existing directory"); // throw exception if it cant find the path
        }
        currentDirectory = dir.getCanonicalPath();                                      // fully qualified path - absolute directory - way back up to root
    }
}

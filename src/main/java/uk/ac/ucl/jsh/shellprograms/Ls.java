package uk.ac.ucl.jsh.shellprograms;

import java.io.*;

public class Ls extends ShellProgram
{
    @Override
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream stdout) throws IOException
    {
        File currDir;
        if (args.length == 0) {
            currDir = new File(currentDirectory);                                       // if there is no argument to ls, it just puts the current directory as the directory to list 
        } else if (args.length == 1) {
            currDir = new File(args[0]);                                         // if there is an argument, list the directory specified by the argument
        } else {
            throw new RuntimeException("ls: too many arguments");                       // otherwise throw an error 
        }
        try {
            File[] listOfFiles = currDir.listFiles();                                   // carries the OS for files present in the directory 
            boolean atLeastOnePrinted = false;                                          // avoids printing new line if no files are present
            for (File file : listOfFiles) {
                if (!file.getName().startsWith(".")) {                                  // hides names that start with a .
                    writer.write(file.getName());                                       // prints it to terminal 
                    writer.write("\t");                                                 // line feed
                    writer.flush();
                    atLeastOnePrinted = true;
                }
            }
            if (atLeastOnePrinted) {                                                    // 
                writer.write(System.getProperty("line.separator"));                     // prints a new line after its done printing 
                writer.flush();
            }
        } catch (NullPointerException e) {
            throw new RuntimeException("ls: no such directory");                        // if it cant find the directory 
        }
    }
}

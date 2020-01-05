package uk.ac.ucl.jsh.shellprograms;

import java.io.*;

public class Ls extends ShellProgram
{
    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stdout);
        File currDir;
        if (args.length == 0)
        {
            currDir = new File(currentDirectory);                                       // if there is no argument to ls, it just puts the current directory as the directory to list 
        }
        else if (args.length == 1)
        {
            currDir = new File(currentDirectory + File.separator + args[0]);                                         // if there is an argument, list the directory specified by the argument
        }
        else
        {
            throw new RuntimeException("ls: too many arguments");                       // otherwise throw an error 
        }

        File[] listOfFiles = currDir.listFiles();                                   // carries the OS for files present in the directory
        boolean atLeastOnePrinted = false;                                          // avoids printing new line if no files are present
        if (listOfFiles == null)
        {
            throw new RuntimeException("ls: no such directory");                        // if it cant find the directory

        }
        else
        {
            for (File file : listOfFiles)
            {
                if (!file.getName().startsWith("."))
                {                                  // hides names that start with a .
                    outputStreamWriter.write(file.getName());                                       // prints it to terminal
                    outputStreamWriter.write("\t");                                                 // line feed
                    outputStreamWriter.flush();
                    atLeastOnePrinted = true;
                }
            }
            if (atLeastOnePrinted)
            {                                                    //
                outputStreamWriter.write(System.getProperty("line.separator"));                     // prints a new line after its done printing
                outputStreamWriter.flush();
            }
        }

    }
}

package uk.ac.ucl.jsh.shellprograms;

import java.io.*;

public class Ls extends ShellProgram
{
    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);
        File currDir;
        if (args.length == 0)
        {
            currDir = new File(currentDirectory);                                       // if there is no argument to ls, it just puts the current directory as the directory to list 
        } else if (args.length == 1)
        {
            currDir = new File(currentDirectory + File.separator + args[0]);                                         // if there is an argument, list the directory specified by the argument
        } else
        {
            throw new RuntimeException("ls: too many arguments");                       // otherwise throw an error 
        }
        try
        {
            File[] listOfFiles = currDir.listFiles();                                   // carries the OS for files present in the directory 
            boolean atLeastOnePrinted = false;                                          // avoids printing new line if no files are present
            for (File file : listOfFiles)
            {
                if (!file.getName().startsWith("."))
                {                                  // hides names that start with a .
                    str_to_bytes.write(file.getName());                                       // prints it to terminal
                    str_to_bytes.write("\t");                                                 // line feed
                    str_to_bytes.flush();
                    atLeastOnePrinted = true;
                }
            }
            if (atLeastOnePrinted)
            {                                                    //
                str_to_bytes.write(System.getProperty("line.separator"));                     // prints a new line after its done printing
                str_to_bytes.flush();
            }
        } catch (NullPointerException e)
        {
            throw new RuntimeException("ls: no such directory");                        // if it cant find the directory 
        }
    }
}

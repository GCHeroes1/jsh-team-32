package uk.ac.ucl.jsh.shellprograms;

import java.io.*;

public class Echo extends ShellProgram
{
    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stdout);
        for (int index = 0; index < args.length; index++)
        {
            outputStreamWriter.write(args[index]);
            if (index != args.length - 1)  // don't print out a space if it's the last argument
            {
                outputStreamWriter.write(" ");
            }
            outputStreamWriter.flush();
        }
        if (args.length > 0)
        {
            outputStreamWriter.write(System.getProperty("line.separator"));
            outputStreamWriter.flush();
        }
        outputStreamWriter.close();
    }
}

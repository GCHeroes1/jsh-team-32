package uk.ac.ucl.jsh.shellprograms;

import java.io.*;

public class Echo extends ShellProgram
{
    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);
        for(int index = 0; index < args.length; index++)
        {
            str_to_bytes.write(args[index]);
            if(index != args.length - 1)  // don't print out a space if it's the last argument
            {
                str_to_bytes.write(" ");
            }
            str_to_bytes.flush();
        }
        if (args.length > 0) {
            str_to_bytes.write(System.getProperty("line.separator"));
            str_to_bytes.flush();
        }
    }
}

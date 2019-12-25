package uk.ac.ucl.jsh.shellprograms;

import uk.ac.ucl.jsh.Jsh;
import java.io.*;

public abstract class ShellProgram extends Jsh
{
    OutputStreamWriter writer = new OutputStreamWriter(System.out);

    abstract public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException;

    public void executeUnsafe(String[] args, InputStream stdin, OutputStream stdout)
    {
        try
        {
            execute(args, stdin, stdout);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

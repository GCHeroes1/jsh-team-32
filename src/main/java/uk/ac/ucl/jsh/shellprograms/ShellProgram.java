package uk.ac.ucl.jsh.shellprograms;

import uk.ac.ucl.jsh.Jsh;
import java.io.*;

public abstract class ShellProgram extends Jsh
{
    OutputStreamWriter writer = new OutputStreamWriter(System.out);

    abstract public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream output) throws IOException;

    public void executeUnsafe(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream output)
    {
        try
        {
            execute(args, stdin, output);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

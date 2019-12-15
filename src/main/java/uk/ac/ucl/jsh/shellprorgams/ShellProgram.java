package uk.ac.ucl.jsh.shellprorgams;

import uk.ac.ucl.jsh.Jsh;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;

public abstract class ShellProgram extends Jsh
{
    OutputStreamWriter writer = new OutputStreamWriter(System.out);

    abstract public void execute(String[] args, OutputStream output) throws IOException;

    public void executeUnsafe(String[] args, OutputStream output)
    {
        try
        {
            execute(args, output);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

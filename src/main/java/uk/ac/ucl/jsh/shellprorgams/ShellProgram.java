package uk.ac.ucl.jsh.shellprorgams;

import uk.ac.ucl.jsh.Jsh;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public abstract class ShellProgram extends Jsh
{
    abstract public void execute(String[] args) throws IOException;

    public void executeUnsafe(String[] args)
    {
        try
        {
            execute(args);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

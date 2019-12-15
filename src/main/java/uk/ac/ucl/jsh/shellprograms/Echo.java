package uk.ac.ucl.jsh.shellprograms;

import java.io.IOException;
import java.io.OutputStream;

public class Echo extends ShellProgram
{
    @Override
    public void execute(String[] args, OutputStream output) throws IOException
    {
        for (String arg : args) {
            writer.write(arg);
            writer.write(" ");
            writer.flush();
        }
        if (args.length > 0) {
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }
}

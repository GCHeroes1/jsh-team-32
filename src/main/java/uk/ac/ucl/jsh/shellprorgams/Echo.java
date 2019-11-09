package uk.ac.ucl.jsh.shellprorgams;

import java.io.IOException;

public class Echo extends ShellProgram
{
    @Override
    public void execute(String[] args) throws IOException
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

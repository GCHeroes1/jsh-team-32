package uk.ac.ucl.jsh.shellprorgams;

import java.io.IOException;
import java.io.OutputStream;

public class Pwd extends ShellProgram
{
    @Override
    public void execute(String[] args, OutputStream output) throws IOException
    {
        writer.write(currentDirectory);
        writer.write(System.getProperty("line.separator"));                             // gets property that defines what separates the directories - actually useless
        writer.flush();                                                                 // writes thing to terminal
    }
}

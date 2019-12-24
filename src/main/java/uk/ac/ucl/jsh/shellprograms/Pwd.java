package uk.ac.ucl.jsh.shellprograms;

import java.io.*;

public class Pwd extends ShellProgram
{
    @Override
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream stdout) throws IOException
    {
        writer.write(currentDirectory);
        writer.write(System.getProperty("line.separator"));                             // gets property that defines what separates the directories - actually useless
        writer.flush();                                                                 // writes thing to terminal
    }
}

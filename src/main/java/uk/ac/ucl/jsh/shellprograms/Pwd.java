package uk.ac.ucl.jsh.shellprograms;

import java.io.*;

public class Pwd extends ShellProgram
{
    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stdout);
        writeLineToOutput(outputStreamWriter, currentDirectory);
    }
}

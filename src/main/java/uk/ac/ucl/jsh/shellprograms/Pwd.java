package uk.ac.ucl.jsh.shellprograms;

import java.io.*;

public class Pwd extends ShellProgram
{
    @Override
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream stdout) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);
        str_to_bytes.write(currentDirectory);
        str_to_bytes.write(System.getProperty("line.separator"));                             // gets property that defines what separates the directories - actually useless
        str_to_bytes.flush();                                                                 // writes thing to terminal
    }
}

package uk.ac.ucl.jsh.shellprograms;

import java.io.*;

public class Pwd extends ShellProgram
{
    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);
        write_line_to_output(str_to_bytes, currentDirectory);
    }
}

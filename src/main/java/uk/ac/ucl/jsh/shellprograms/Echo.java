package uk.ac.ucl.jsh.shellprograms;

import java.io.*;

public class Echo extends ShellProgram
{
    @Override
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream output) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(output);
        for (String arg : args) {
            str_to_bytes.write(arg);
            str_to_bytes.write(" ");
            str_to_bytes.flush();
        }
        if (args.length > 0) {
            str_to_bytes.write(System.getProperty("line.separator"));
            str_to_bytes.flush();
        }
        //output.close();
    }
}

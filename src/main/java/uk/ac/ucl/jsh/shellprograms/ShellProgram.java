package uk.ac.ucl.jsh.shellprograms;

import uk.ac.ucl.jsh.Jsh;

import java.io.*;

public abstract class ShellProgram extends Jsh
{
    abstract public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException;

    public void executeUnsafe(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        try
        {
            execute(args, stdin, stdout);
        } catch (Exception e)
        {
            OutputStreamWriter osw = new OutputStreamWriter(stdout);
            osw.write(e.toString());
            osw.write(System.getProperty("line.separator"));
            osw.flush();
        }
    }

    void readToOutput(OutputStreamWriter output, BufferedReader reader) throws IOException
    {
        String line;
        while ((line = reader.readLine()) != null)
        {
            writeLineToOutput(output, line);
        }
        reader.close();
    }

    void writeLineToOutput(OutputStreamWriter output, String string) throws IOException
    {
        output.write(string);
        output.write(System.getProperty("line.separator"));
        output.flush();
    }
}

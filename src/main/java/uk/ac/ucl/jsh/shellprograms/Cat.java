package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Cat extends ShellProgram
{
    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stdout);
        if (args.length == 0)
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
            read_to_output(outputStreamWriter, reader);
        }
        else
        {
            for (String file : args)
            {
                BufferedReader reader;
                try
                {
                    Path filePath = Paths.get(currentDirectory + File.separator + file);
                    reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
                }
                catch (IOException e)
                {
                    throw new IOException("cat: cannot cat file: " + file, e);
                }
                read_to_output(outputStreamWriter, reader);
            }
        }
    }


}

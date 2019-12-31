package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.Charset;
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
            for (String arg : args)
            {
                Path filePath = Paths.get(currentDirectory + File.separator + arg);
                BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
                read_to_output(outputStreamWriter, reader);
            }
        }
    }

    private void read_to_output(OutputStreamWriter str_to_bytes, BufferedReader reader) throws IOException
    {
        String line;
        while ((line = reader.readLine()) != null)
        {
            str_to_bytes.write(line);
            str_to_bytes.write(System.getProperty("line.separator"));
            str_to_bytes.flush();
        }
        reader.close();
    }
}

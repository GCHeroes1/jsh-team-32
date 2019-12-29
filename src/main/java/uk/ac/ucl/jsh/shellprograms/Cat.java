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
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);
        if (args.length == 0) {
            String line;
            BufferedReader bfr = new BufferedReader(new InputStreamReader(stdin));
            while((line = bfr.readLine()) != null)
            {
                str_to_bytes.write(line);
                str_to_bytes.write(System.getProperty("line.separator"));
                str_to_bytes.flush();
            }
        }
        else
        {
            for (String arg : args)
            {
                Charset encoding = StandardCharsets.UTF_8;
                Path filePath = Paths.get(currentDirectory + File.separator + arg);
                try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        str_to_bytes.write(line);
                        str_to_bytes.write(System.getProperty("line.separator"));
                        str_to_bytes.flush();
                    }
                }
                catch (IOException e)
                {
                    throw new RuntimeException("cat: cannot open " + arg);
                }
            }
        }
    }
}

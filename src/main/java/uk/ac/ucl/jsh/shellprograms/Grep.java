package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Grep extends ShellProgram
{
    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stdout);
        Pattern grepPattern = Pattern.compile(args[0]);

        if (args.length == 1)
        {
            String line;
            BufferedReader bfr = new BufferedReader(new InputStreamReader(stdin));
            while ((line = bfr.readLine()) != null)
            {
                Matcher matcher = grepPattern.matcher(line);
                if (matcher.find())
                {
                    writeLineToOutput(outputStreamWriter, line);
                }
            }

        } else // read the file instead
        {
            int numOfFiles = args.length - 1;
            Path filePath;
            BufferedReader reader;
            Path currentDir = Paths.get(currentDirectory);
            Charset encoding = StandardCharsets.UTF_8;
            for (int i = 0; i < numOfFiles; i++)
            {
                filePath = currentDir.resolve(args[i + 1]);
                try
                {
                    reader = Files.newBufferedReader(filePath, encoding);
                }
                catch (IOException e)
                {
                    throw new IOException("grep: wrong file argument", e);
                }
                String line;
                while ((line = reader.readLine()) != null)
                {
                    Matcher matcher = grepPattern.matcher(line);
                    if (matcher.find())
                    {
                        writeLineToOutput(outputStreamWriter, line);
                    }
                }
            }

        }

    }
}

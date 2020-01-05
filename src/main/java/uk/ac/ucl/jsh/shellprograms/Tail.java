package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Tail extends ShellProgram
{
    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stdout);
        BufferedReader reader;
        int tailLines = 10; //default number of lines


        if (args.length > 3)
        {
            throw new RuntimeException("tail: wrong arguments");
        }
        if (args.length == 3 && !args[0].equals("-n"))
        {
            throw new RuntimeException("tail: wrong argument " + args[0]);
        }

        String tailArg;
        switch (args.length)
        {
            case 3:
                try
                {
                    tailLines = Integer.parseInt(args[1]);
                } catch (Exception e)
                {
                    throw new RuntimeException("tail: wrong argument " + args[1]);
                }
                tailArg = args[2];
                break;
            case 2:
                try
                {
                    tailLines = Integer.parseInt(args[1]);
                } catch (Exception e)
                {
                    throw new RuntimeException("tail: wrong argument " + args[1]);
                }
                reader = new BufferedReader(new InputStreamReader(stdin));
                countAndWrite(reader, outputStreamWriter, tailLines);
                return;
            case 1:
                tailArg = args[0];
                break;
            default:
                reader = new BufferedReader(new InputStreamReader(stdin));
                countAndWrite(reader, outputStreamWriter, tailLines);
                return;
        }

        Path filePath = Paths.get(currentDirectory + File.separator + tailArg);
        try (BufferedReader fileReader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8))
        {
            countAndWrite(fileReader, outputStreamWriter, tailLines);
        }
        catch (IOException e)
        {
            throw new RuntimeException("tail: cannot open " + tailArg);
        }
    }

    private void countAndWrite(BufferedReader reader, OutputStreamWriter outputStreamWriter, int tailLines) throws IOException
    {
        String line;
        ArrayList<String> lines = new ArrayList<>();
        while ((line = reader.readLine()) != null)
        {
            lines.add(line);
        }
        reader.close();

        if (lines.size() > 0)
        {
            int index = Math.max(lines.size() - tailLines, 0);
            for (; index < lines.size(); index++)
            {
                writeLineToOutput(outputStreamWriter, lines.get(index));
            }
        }
    }
}

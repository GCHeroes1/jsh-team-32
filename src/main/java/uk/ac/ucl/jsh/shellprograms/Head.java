package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Head extends ShellProgram
{
    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stdout);
        int headLines = 10; //spit out 10 lines by default

        if (args.length > 3)
        {
            throw new RuntimeException("head: wrong arguments");
        }
        else if (args.length == 3 && !args[0].equals("-n"))
        {
            throw new RuntimeException("head: wrong argument " + args[0]);

        }
        String headArg;
        BufferedReader bfr;
        switch (args.length)
        {
            case 3:
                try
                {
                    headLines = Integer.parseInt(args[1]);
                } catch (Exception e)
                {
                    throw new RuntimeException("head: wrong argument " + args[1]);
                }
                headArg = args[2];
                break;

            case 2:
                try
                {
                    headLines = Integer.parseInt(args[1]);
                } catch (Exception e)
                {
                    throw new RuntimeException("head: wrong argument " + args[1]);
                }
                bfr = new BufferedReader(new InputStreamReader(stdin));
                write_n_lines(outputStreamWriter, headLines, bfr);
                return;

            case 1:
                headArg = args[0];
                break;

            default:
                // no reason to throw IOException when reading from a bytearrayinputstream
                bfr = new BufferedReader(new InputStreamReader(stdin));
                write_n_lines(outputStreamWriter, headLines, bfr);
                return;
        }


        Path filePath = Paths.get(currentDirectory + File.separator + headArg);
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8))
        {
            write_n_lines(outputStreamWriter, headLines, reader);
        }
        catch (IOException e)
        {
            throw new IOException("head: cannot open " + headArg, e);
        }
    }

    private void write_n_lines(OutputStreamWriter str_to_bytes, int headLines, BufferedReader reader) throws IOException
    {
        for (int i = 0; i < headLines; i++)
        {
            String line;
            if ((line = reader.readLine()) != null)
            {
                writeLineToOutput(str_to_bytes, line);
            }
        }
    }
}

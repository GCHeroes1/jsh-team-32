

package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class Sed extends ShellProgram
{
    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stdout);

        String regexString = args[0];
        String target;
        char separator;
        boolean global = false;
        if (regexString.charAt(0) != 's')
        {
            throw new RuntimeException("sed: regex pattern not starting with an 's'");
        }
        else
        {
            try
            {
                separator = regexString.charAt(1);
                String[] regexSplit = regexString.split(Pattern.quote(String.valueOf(separator)));
                if (regexSplit.length == 3)
                {
                    target = regexSplit[2];
                    regexString = regexSplit[1];
                }
                else if (regexSplit.length == 4 && regexSplit[3].equals("g"))
                {
                    target = regexSplit[2];
                    regexString = regexSplit[1];
                    global = true;
                }
                else
                {
                    throw new RuntimeException("sed: invalid pattern provided");
                }
            }
            catch (StringIndexOutOfBoundsException e)
            {
                throw new RuntimeException("sed: bad pattern", e);
            }

        }


        if (args.length == 1) // use stdin as the file argument is not specified
        {
            BufferedReader bfr = new BufferedReader(new InputStreamReader(stdin));
            findAndReplace(outputStreamWriter, regexString, target, global, bfr);
        }
        else if (args.length == 2)
        {
            Path filePath;
            Path currentDir = Paths.get(currentDirectory);
            filePath = currentDir.resolve(args[1]);
            try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8))
            {
                findAndReplace(outputStreamWriter, regexString, target, global, reader);
            }
            catch (IOException e)
            {
                throw new IOException("sed: wrong file argument", e);
            }
        }
        else
        {
            throw new RuntimeException("sed: wrong number of arguments given");
        }
    }

    private void findAndReplace(OutputStreamWriter outputStreamWriter, String regexString, String target, boolean global, BufferedReader reader) throws IOException
    {
        String line;
        String result;

        while ((line = reader.readLine()) != null)
        {
            result = global ?
                    Pattern.compile(regexString).matcher(line).replaceAll(target) :
                    Pattern.compile(regexString).matcher(line).replaceFirst(target);
            writeLineToOutput(outputStreamWriter, result);
        }
    }

}

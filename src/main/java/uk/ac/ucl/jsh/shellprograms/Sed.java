

package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.Charset;
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
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);

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
            separator = regexString.charAt(1);
            String[] regex_split = regexString.split(Pattern.quote(String.valueOf(separator)));
            if (regex_split.length == 3)
            {
                target = regex_split[2];
                regexString = regex_split[1];
            }
            else if (regex_split.length == 4 && regex_split[3].equals("g"))
            {
                target = regex_split[2];
                regexString = regex_split[1];
                global = true;
            }
            else
            {
                throw new RuntimeException("sed: invalid pattern provided");
            }
        }


        if (args.length == 1) // use stdin as the file argument is not specified
        {
            BufferedReader bfr = new BufferedReader(new InputStreamReader(stdin));
            find_and_replace(str_to_bytes, regexString, target, global, bfr);
        }
        else if (args.length == 2)
        {
            Path filePath;
            Path currentDir = Paths.get(currentDirectory);
            Charset encoding = StandardCharsets.UTF_8;
            filePath = currentDir.resolve(args[1]);
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding))
            {
                find_and_replace(str_to_bytes, regexString, target, global, reader);
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

    private void find_and_replace(OutputStreamWriter str_to_bytes, String regexstring, String target, boolean global, BufferedReader reader) throws IOException
    {
        String line;
        String result;

        while ((line = reader.readLine()) != null)
        {
            result = global ?
                    Pattern.compile(regexstring).matcher(line).replaceAll(target) :
                    Pattern.compile(regexstring).matcher(line).replaceFirst(target);
            write_line_to_output(str_to_bytes, result);
        }
    }

}

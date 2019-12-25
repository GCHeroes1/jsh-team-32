

package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sed extends ShellProgram
{
    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);
        Pattern grepPattern = Pattern.compile(args[0]);


        //int n = stdin.available();
        //byte[] a = new byte[n];
        //stdin.read(a, 0, n);
        //System.out.println(new String(a));


        String regexstring = args[0];
        String target, result;
        char separator;
        boolean global = false;
        if(regexstring.charAt(0) != 's')
        {
            throw new RuntimeException("sed: regex pattern not starting with an 's'");
        }
        else
        {
            separator = regexstring.charAt(1);
            String[] regex_split = regexstring.split(Pattern.quote(String.valueOf(separator)));
            if(regex_split.length == 3)
            {
                target = regex_split[2];
                regexstring = regex_split[1];
            }
            else if(regex_split.length == 4 && regex_split[3].equals("g"))
            {
                target = regex_split[2];
                regexstring = regex_split[1];
                global = true;
            }
            else
            {
                throw new RuntimeException("sed: invalid pattern provided");
            }
        }


        if (args.length == 1) // use stdin as the file argument is not specified
        {
            String line = null;
            BufferedReader bfr = new BufferedReader(new InputStreamReader(stdin));
            find_and_replace(str_to_bytes, regexstring, target, global, bfr);
        }
        else if(args.length == 2)
        {
            Path filePath;
            Path currentDir = Paths.get(currentDirectory);
            filePath = currentDir.resolve(args[1]);
            if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                    !Files.exists(filePath) || !Files.isReadable(filePath)) {
                throw new RuntimeException("sed: wrong file argument");
            }

            Charset encoding = StandardCharsets.UTF_8;
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                find_and_replace(str_to_bytes, regexstring, target, global, reader);
            } catch (IOException e) {
                throw new RuntimeException("grep: cannot open " + args[1]);
            }

        }
        else
        {
            throw new RuntimeException("sed: wrong number of arguments given");
        }




    }

    private void find_and_replace(OutputStreamWriter str_to_bytes, String regexstring, String target, boolean global, BufferedReader reader) throws IOException {
        String line;
        String result;

        while ((line = reader.readLine()) != null) {
            if(global)
            {
                result = Pattern.compile(regexstring).matcher(line).replaceAll(target);
            }
            else
            {
                result = Pattern.compile(regexstring).matcher(line).replaceFirst(target);
            }
            str_to_bytes.write(result);
            str_to_bytes.write(System.getProperty("line.separator"));
            str_to_bytes.flush();
        }
    }

}

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
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream stdout) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);
        Pattern grepPattern = Pattern.compile(args[0]);

        if (args.length == 1) {
            String line;
            BufferedReader bfr = new BufferedReader(new InputStreamReader(stdin));
            while((line = bfr.readLine()) != null)
            {
                Matcher matcher = grepPattern.matcher(line);
                if(matcher.find())
                {
                    str_to_bytes.write(line);
                    str_to_bytes.write(System.getProperty("line.separator"));
                    str_to_bytes.flush();
                }
            }

        }
        else // read the file instead
        {
            int numOfFiles = args.length - 1;
            Path filePath;
            Path[] filePathArray = new Path[numOfFiles];
            Path currentDir = Paths.get(currentDirectory);
            for (int i = 0; i < numOfFiles; i++) {
                filePath = currentDir.resolve(args[i + 1]);
                if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                        !Files.exists(filePath) || !Files.isReadable(filePath)) {
                    throw new RuntimeException("grep: wrong file argument");
                }
                filePathArray[i] = filePath;
            }
            for (int j = 0; j < filePathArray.length; j++) {
                Charset encoding = StandardCharsets.UTF_8;
                try (BufferedReader reader = Files.newBufferedReader(filePathArray[j], encoding)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = grepPattern.matcher(line);
                        if (matcher.find()) {
                            str_to_bytes.write(line);
                            str_to_bytes.write(System.getProperty("line.separator"));
                            str_to_bytes.flush();
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("grep: cannot open " + args[j + 1]);
                }
            }
        }

    }
}

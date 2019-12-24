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
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream output) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(output);
        Pattern grepPattern = Pattern.compile(args[0]);

        //int n = stdin.available();
        //byte[] a = new byte[n];
        //stdin.read(a, 0, n);
        //System.out.println(new String(a));
        if (args.length == 1) {
            //throw new RuntimeException("grep: wrong number of arguments");
            //line above not valid anymore. 1 arg == use stdin
            String line = null;
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
        else
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
                    String line = null;
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

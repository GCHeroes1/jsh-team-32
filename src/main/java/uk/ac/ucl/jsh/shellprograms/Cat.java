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
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream stdout) throws IOException
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
        } else {
            for (String arg : args) {                                                // for each file specified in the arguments 
                Charset encoding = StandardCharsets.UTF_8;                              // print it using UTF 8 
                File currFile = new File(currentDirectory + File.separator + arg);      // gets the absolute path of the file
                if (currFile.exists()) {                                                // checks if it exists 
                    Path filePath = Paths.get(currentDirectory + File.separator + arg); // gets a path object from the filepath 
                    try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) { // tries offering a buffered reader on the file 
                        String line = null;                                             // initialises the line variable 
                        while ((line = reader.readLine()) != null) {                    // for each line that isnt empty in the file 
                            str_to_bytes.write(String.valueOf(line));                         // print the contents
                            str_to_bytes.write(System.getProperty("line.separator"));         // necessary
                            str_to_bytes.flush();
                        }
                    } catch (IOException e) {                                           //
                        throw new RuntimeException("cat: cannot open " + arg);          //
                    }
                } else {
                    throw new RuntimeException("cat: file does not exist");             //
                }
            }
        }
    }
}

package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Head extends ShellProgram
{
    @Override
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream output) throws IOException
    {
        if (args.length == 0) {                                                       
            throw new RuntimeException("head: missing arguments");
        }
        if (args.length < 0 && args.length > 3) {  
            throw new RuntimeException("head: wrong arguments"); 
        }
        if (args.length == 3 && !args[0].equals("-n")) {                     
            throw new RuntimeException("head: wrong argument " + args[0]);      
        }
        // if no file specified (if 2 args where arg[0] == '-n' and arg[1] is an int, or 0 arg) use stdin
        //if no number specified (if 1 arg where arg[0] is a file or 0 arg) use 10
        int headLines = 10; //This is the default number of lines parsed                                                      
        String headArg;
        if (args.length == 3) {                                                     
            try {
                headLines = Integer.parseInt(args[1]);                          
            } catch (Exception e) {
                throw new RuntimeException("head: wrong argument " + args[1]);  
            }
            headArg = args[2];
        } else if (args.length == 2){
            try {
                headLines = Integer.parseInt(args[1]);                          
            } catch (Exception e) {
                throw new RuntimeException("head: wrong argument " + args[1]);  
            }
            headArg = stdin; //fix this pls alex
        } else if (args.length == 1){
            headArg = args[0];
        } else if (args.length == 0){
            headArg = stdin; //fix this pls alex
        }

            headArg = args[0];
        }
        File headFile = new File(currentDirectory + File.separator + headArg);         
        if (headFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            Path filePath = Paths.get((String) currentDirectory + File.separator + headArg);
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                for (int i = 0; i < headLines; i++) {
                    String line = null;
                    if ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("head: cannot open " + headArg);
            }
        } else {
            throw new RuntimeException("head: " + headArg + " does not exist");
        }
    }

}

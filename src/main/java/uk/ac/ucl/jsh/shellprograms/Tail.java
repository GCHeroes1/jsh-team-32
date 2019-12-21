package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Tail extends ShellProgram
{
    @Override
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream output) throws IOException
    {
        /*if (args.length == 0) {
            throw new RuntimeException("tail: missing arguments");
        }*/ 
        // Correct me if I'm wrong but theoretically if there's 0 args it should print the last 10 lines of stdin?
        if (args.length < 0 || args.length > 3) {
            throw new RuntimeException("tail: wrong arguments");
        }
        if (args.length == 3 && !args[0].equals("-n")) {
            throw new RuntimeException("tail: wrong argument " + args[0]);
        }
        int tailLines = 10;//default number of lines
        String tailArg;
        if (args.length == 3) {
            try {
                tailLines = Integer.parseInt(args[1]);
            } catch (Exception e) {
                throw new RuntimeException("tail: wrong argument " + args[1]);
            }
            tailArg = args[2];
        } else if (args.length == 2){
            try {
                tailLines = Integer.parseInt(args[1]);
            } catch (Exception e) {
                throw new RuntimeException("tail: wrong argument " + args[1]);
            }
            tailArg = stdin; //fix this pls alex
        } else if (args.length == 1){
            tailArg = args[0];
        } else if (args.length == 0){
        tailArg = stdin; //fix this pls alex
        }
        //make sure if there's less than N lines it just prints them all without raising an exception
        File tailFile = new File(currentDirectory + File.separator + tailArg);
        if (tailFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            Path filePath = Paths.get((String) currentDirectory + File.separator + tailArg);
            ArrayList<String> storage = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    storage.add(line);
                }
                int index = 0;
                if (tailLines > storage.size()) {
                    index = 0;
                } else {
                    index = storage.size() - tailLines;
                }
                for (int i = index; i < storage.size(); i++) {
                    writer.write(storage.get(i) + System.getProperty("line.separator"));
                    writer.flush();
                }
            } catch (IOException e) {
                throw new RuntimeException("tail: cannot open " + tailArg);
            }
        } else {
            throw new RuntimeException("tail: " + tailArg + " does not exist");
        }
    }
}

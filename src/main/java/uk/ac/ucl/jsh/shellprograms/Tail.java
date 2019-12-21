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
            tailArg = "stdin";
        } else if (args.length == 1){
            tailArg = args[0];
        } else if (args.length == 0){
        tailArg = "stdin";
        }
        if (tailArg.equals("stdin")){
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
            int lines = 0;
            while (reader.readLine() != null) lines++;
            reader.close();
            //this is very inefficient and I'm sorry
            BufferedReader bfr = new BufferedReader(new InputStreamReader(stdin));
            for (int i = (lines - tailLines); i < lines; i++){
                String line = null;
                if ((line = reader.readLine()) != null) {
                    str_to_bytes.write(line);
                    str_to_bytes.write(System.getProperty("line.separator"));
                    str_to_bytes.flush();
                }
            }
        }
        else{
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
}
// also have not tested this yet. potentially should close our buffer readers.
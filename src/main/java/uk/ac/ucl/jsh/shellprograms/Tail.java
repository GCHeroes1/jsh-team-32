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
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream stdout) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);

        if (args.length > 3) {
            throw new RuntimeException("tail: wrong arguments");
        }
        if (args.length == 3 && !args[0].equals("-n")) {
            throw new RuntimeException("tail: wrong argument " + args[0]);
        }
        int tailLines = 10; //default number of lines
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
        }
        else
        {
        tailArg = "stdin";
        }
        if (tailArg.equals("stdin")){
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
            ArrayList<String> lines = new ArrayList<>();
            while ((line = reader.readLine()) != null) lines.add(line);
            reader.close();
            //this is very inefficient and I'm sorry
            if(lines.size() > 0)
            {
                int index = Math.max(lines.size() - tailLines, 0);
                for (; index < lines.size(); index++){
                    str_to_bytes.write(lines.get(index));
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
                    String line;
                    while ((line = reader.readLine()) != null) {
                        storage.add(line);
                    }
                    int index = 0;
                    if (tailLines < storage.size())
                    {
                        index = storage.size() - tailLines;
                    }
                    for (int i = index; i < storage.size(); i++) {
                        str_to_bytes.write(storage.get(i));
                        str_to_bytes.write(System.getProperty("line.separator"));
                        str_to_bytes.flush();
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
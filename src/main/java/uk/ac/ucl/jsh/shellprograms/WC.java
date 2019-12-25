
package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WC extends ShellProgram {
    private int countChar(Reader file) throws IOException {
        BufferedReader reader = new BufferedReader(file);
        int charCount = reader.readLine().length();
      return charCount;
    }
    private int countWord(Reader file) throws IOException {
        BufferedReader reader = new BufferedReader(file);     
        String line = reader.readLine();
        int count = 0;
        while (line != null) {
            String [] words = line.split(" ");
            for( String x : words)
            {
                count++;
            }
            line = reader.readLine();
       }     
       return count;    
    }
    private int countLines(Reader file) throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(file);
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }

    @Override
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream stdout) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);
        Path filePath;
        Path currentDir = Paths.get(currentDirectory);
        Charset encoding = StandardCharsets.UTF_8;
            if (args[0].equals("-m")){
            if (args.length > 1){
                filePath = Paths.get((String) currentDirectory + File.separator + args[1]);
                if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                    !Files.exists(filePath) || !Files.isReadable(filePath)) {
                    throw new RuntimeException("sed: wrong file argument");
                }
                BufferedReader reader = Files.newBufferedReader(filePath, encoding);
                int char_ = countChar(reader);
                //int char_ = countChar(new InputStreamReader(stdin));
                String char_count = String.valueOf(char_);
                str_to_bytes.write(char_count); 
             }
        }
        else if(args[0].equals("-w")){
            if (args.length > 1){
                int word_ = countWord(new FileReader(args[1]));
                String word_count = String.valueOf(word_);
                str_to_bytes.write(word_count);
             }
            //  else if (args.length == 2){
            //     str_to_bytes.write(countWord(new FileReader(args[1]))); 
            //   }
        }
        else if(args[0].equals("-l")){
            if (args.length > 1){
                int lines = countLines(new FileReader(args[1]));
                String lines_count = String.valueOf(lines);
                str_to_bytes.write(lines_count); 
            }
            // else if (args.length == 2){
            //     str_to_bytes.write(countLines(new FileReader(args[1]))); 
            //  }
        }
        else{
            throw new RuntimeException("wc: wrong arguments");
        }
        str_to_bytes.write(System.getProperty("line.separator"));
        str_to_bytes.flush();
    }
}
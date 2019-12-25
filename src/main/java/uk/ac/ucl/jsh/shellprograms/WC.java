
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
        int charCount = 0;
        String line;
        while((line = reader.readLine()) != null){
            if(!(line.equals(""))){
                charCount += line.length();
            }
        }
      return charCount;
    }
    private int countWord(Reader file) throws IOException {
        BufferedReader reader = new BufferedReader(file);   
        int count = 0;  
        String line;
        while((line = reader.readLine()) != null){
            if(!(line.equals(""))){
                String[] wordList = line.split(" "); 
                count += wordList.length; 
            }
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
        int fileArgument = 1;
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);
        Path filePath;
        String wcArg = "";
        Charset encoding = StandardCharsets.UTF_8;
        if (args.length == 1){
            wcArg = "stdin";
        }
        if (args.length > 1 && (!args[0].equals("-m") && !args[0].equals("-w") && !args[0].equals("-l") && !args[1].equals("<"))){
            // System.out.println("i threw up");
            throw new RuntimeException("wc: wrong argument " + args[0]);
        }
        if (args[1].equals("<")){
            // IO redirection?? can just skip it and process because it comes after the option 
            // System.out.println("i redirected");
            fileArgument++;
        }
        if (args[0].equals("-m")){
            // System.out.println("i recognised -m");
            if (args.length > 1){
                // System.out.println("there are more than 2 arguments with -m");
                filePath = Paths.get((String) currentDirectory + File.separator + args[fileArgument]);
                if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                    !Files.exists(filePath) || !Files.isReadable(filePath)) {
                    throw new RuntimeException("sed: wrong file argument");
                }
                BufferedReader reader = Files.newBufferedReader(filePath, encoding);
                int char_ = countChar(reader);
                String char_count = String.valueOf(char_);
                str_to_bytes.write(char_count); 
             }
        }
        else if(args[0].equals("-w")){
            // System.out.println("i recognised -w");
            if (args.length > 1){
                // System.out.println("there are more than 2 arguments with -w");
                filePath = Paths.get((String) currentDirectory + File.separator + args[fileArgument]);
                if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                    !Files.exists(filePath) || !Files.isReadable(filePath)) {
                    throw new RuntimeException("sed: wrong file argument");
                }
                BufferedReader reader = Files.newBufferedReader(filePath, encoding);
                int word_ = countWord(reader);
                String word_count = String.valueOf(word_);
                str_to_bytes.write(word_count);
             }
        }
        else if(args[0].equals("-l")){
            // System.out.println("i recognised -l");
            if (args.length > 1){
                // System.out.println("there are more than 2 arguemnts with -l");
                filePath = Paths.get((String) currentDirectory + File.separator + args[fileArgument]);
                if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                    !Files.exists(filePath) || !Files.isReadable(filePath)) {
                    throw new RuntimeException("sed: wrong file argument");
                }
                BufferedReader reader = Files.newBufferedReader(filePath, encoding);
                int lines = countLines(reader);
                String lines_count = String.valueOf(lines);
                str_to_bytes.write(lines_count); 
            }
        }

        // it never gets to here?! 
        // else{
        if (wcArg.equals("stdin")){
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
            // System.out.println("i recognsied that there was no option selected"); // DOESNT GET TO THIS 
            // filePath = Paths.get((String) currentDirectory + File.separator + args[0]);
            // if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                // !Files.exists(filePath) || !Files.isReadable(filePath)) {
                // throw new RuntimeException("sed: wrong file argument");
            // }
            // BufferedReader reader = Files.newBufferedReader(filePath, encoding);
            String char_count = String.valueOf(countChar(reader));
            String word_count = String.valueOf(countWord(reader));
            String lines_count = String.valueOf(countLines(reader));
            str_to_bytes.write(char_count);
            str_to_bytes.write(word_count);
            str_to_bytes.write(lines_count);
            // there was no option, just print everything 
        }
        str_to_bytes.write(System.getProperty("line.separator"));
        str_to_bytes.flush();
        // System.out.println("i skipped everything??"); 
    }
}
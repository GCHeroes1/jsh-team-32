
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
            charCount += line.length() + 1;
        }
      return charCount;
    }
    private int countWord(Reader file) throws IOException { // works fine, dont need to touch it (i dont think so anyway)
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

    private int countLines(Reader file) throws FileNotFoundException, IOException{ // works fine
        BufferedReader reader = new BufferedReader(file);
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }

    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        int fileArgument = 1; //hacky
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);
        // necessary for reading the file from the input
        Path filePath;
        String wcArg = "";
        Charset encoding = StandardCharsets.UTF_8;
        if (args.length == 1){ //ughh i was trying to fix the ability to check if no option was given so that you need to run all 3 options and output as a string array
            wcArg = "stdin";
        }
        if (args.length > 1 && (!args[0].equals("-m") && !args[0].equals("-w") && !args[0].equals("-l"))){
            throw new RuntimeException("wc: wrong argument " + args[0]);
        }
        if (args[0].equals("-m")){ //checks that there is an arg length of 2 or more, so there's an option + file included (need to expand with a for loop to iterate through multiple given files and
            // add together the results and output it as a single number, thats why i used "fileArgument" instread of the magic number (1), coz it should allow for multiple files (as stated in spec)
            if (args.length > 1){
                filePath = Paths.get((String) currentDirectory + File.separator + args[fileArgument]);
                if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                    !Files.exists(filePath) || !Files.isReadable(filePath)) {
                    throw new RuntimeException("wc: wrong file argument");
                }
                // the previous method for outputting the result of the count wasn't quite right, this is the correct method
                BufferedReader reader = Files.newBufferedReader(filePath, encoding);
                int char_ = countChar(reader);
                String char_count = String.valueOf(char_);
                str_to_bytes.write(char_count); 
             }
        }
        else if(args[0].equals("-w")){
            // there's a lot of refactoring that can be done, since a lot of the stuff is copy pasted, but i wanted to implement the for loop first and then refactor, up to you though, it would probably
            // save more time to refactor at this stage, just be mindful that a for loop is necessary
            if (args.length > 1){
                filePath = Paths.get((String) currentDirectory + File.separator + args[fileArgument]);
                if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                    !Files.exists(filePath) || !Files.isReadable(filePath)) {
                    throw new RuntimeException("wc: wrong file argument");
                }
                BufferedReader reader = Files.newBufferedReader(filePath, encoding);
                int word_ = countWord(reader);
                String word_count = String.valueOf(word_);
                str_to_bytes.write(word_count);
             }
        }
        else if(args[0].equals("-l")){
            if (args.length > 1){
                filePath = Paths.get((String) currentDirectory + File.separator + args[fileArgument]);
                if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                    !Files.exists(filePath) || !Files.isReadable(filePath)) {
                    throw new RuntimeException("wc: wrong file argument");
                }
                BufferedReader reader = Files.newBufferedReader(filePath, encoding);
                int lines = countLines(reader);
                String lines_count = String.valueOf(lines);
                str_to_bytes.write(lines_count);
            }
        }
        // else{
        if (wcArg.equals("stdin")){
             filePath = Paths.get((String) currentDirectory + File.separator + args[0]);
            if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                !Files.exists(filePath) || !Files.isReadable(filePath)) {
                throw new RuntimeException("wc: wrong file argument");
            }
            BufferedReader reader;
            // should be lines, then words, then bytes
            reader = Files.newBufferedReader(filePath, encoding);
            String char_count = String.valueOf(countChar(reader));
            reader = Files.newBufferedReader(filePath, encoding);
            String word_count = String.valueOf(countWord(reader));
            reader = Files.newBufferedReader(filePath, encoding);
            String lines_count = String.valueOf(countLines(reader));
            str_to_bytes.write(lines_count);
            str_to_bytes.write("\t");
            str_to_bytes.write(word_count);
            str_to_bytes.write("\t");
            str_to_bytes.write(char_count);
        }
        str_to_bytes.write(System.getProperty("line.separator"));
        str_to_bytes.flush();
    }
}
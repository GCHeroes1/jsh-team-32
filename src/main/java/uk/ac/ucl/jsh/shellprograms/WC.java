package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WC extends ShellProgram {
    private int[] counting(Reader file) throws IOException {
        BufferedReader reader = new BufferedReader(file);
        int charCount = 0;
        int wordCount = 0;
        int lineCount = 0;
        String line;
        while((line = reader.readLine()) != null){
            charCount += line.length() + 1;
            if(!(line.equals(""))){
                String[] wordList = line.split(" ");
                wordCount += wordList.length;
            }
            lineCount++;
        }
        System.out.println(charCount);
        System.out.println(wordCount);
        System.out.println(lineCount);
        return new int[]{charCount, wordCount, lineCount};
        }

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
        int fileArgument = 0; //hacky
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);
        Path filePath;
        String wcArg = "";
        Charset encoding = StandardCharsets.UTF_8;
        if (args.length == 1){
            wcArg = "stdin";
        }
        if (args.length > 1 && (!args[0].equals("-m") && !args[0].equals("-w") && !args[0].equals("-l"))){
            throw new RuntimeException("wc: wrong argument " + args[0]);
        }
        if (args.length > 1){
            fileArgument = 1;
        }
        int[] countArray = new int[]{0, 0, 0};
        for (int i = fileArgument; i < (args.length); i++){
            filePath = Paths.get((String) currentDirectory + File.separator + args[fileArgument]);
            if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                    !Files.exists(filePath) || !Files.isReadable(filePath)) {
                throw new RuntimeException("wc: wrong file argument");
            }
            BufferedReader reader = Files.newBufferedReader(filePath, encoding);
            int[] currentCounting = counting(reader);
            for (int j = 0; j < 3; j++) {
                countArray[j] += currentCounting[j];
            }
        }
        switch (args[0]) {
            case "-m":
                if (args.length > 1) {
                    String char_count = String.valueOf(countArray[0]);
                    str_to_bytes.write(char_count);
                }
                break;
            case "-w":
                if (args.length > 1) {
                    String word_count = String.valueOf(countArray[1]);
                    str_to_bytes.write(word_count);
                }
                break;
            case "-l":
                if (args.length > 1) {
                    String lines_count = String.valueOf(countArray[2]);
                    str_to_bytes.write(lines_count);
                }
                break;
        }
        // else{
        if (wcArg.equals("stdin")){
            str_to_bytes.write(countArray[2]);
            str_to_bytes.write("\t");
            str_to_bytes.write(countArray[1]);
            str_to_bytes.write("\t");
            str_to_bytes.write(countArray[0]);
        }
        str_to_bytes.write(System.getProperty("line.separator"));
        str_to_bytes.flush();
    }
}
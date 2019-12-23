
package uk.ac.ucl.jsh.shellprograms;

import java.io.*;

public class WC extends ShellProgram {
    private int countChar(Reader file) {
        BufferedReader reader = new BufferedReader(file);
        int charCount = 0;
        String data = " ";
        while (data != null) {
            try {
                data = reader.readLine();
            } catch (IOException e) {
                
                e.printStackTrace();
            }
            charCount += data.length();
        }
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
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream output) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(output);
        if (args.length < 1 || args.length > 2){
            throw new RuntimeException("wc: wrong number of arguments");
        }
        if (args[0] == "-m"){
            if (args.length == 1){
                str_to_bytes.write(countChar(new InputStreamReader(stdin))); 
             }
             else if (args.length == 2){
                 str_to_bytes.write(countChar(new FileReader(args[1]))); 
              }
        }
        else if(args[0] == "-w"){
            if (args.length == 1){
                str_to_bytes.write(countWord(new InputStreamReader(stdin))); 
             }
             else if (args.length == 2){
                 str_to_bytes.write(countWord(new FileReader(args[1]))); 
              }
        }
        else if(args[0] == "-l"){
            if (args.length == 1){
               str_to_bytes.write(countLines(new InputStreamReader(stdin))); 
            }
            else if (args.length == 2){
                str_to_bytes.write(countLines(new FileReader(args[1]))); 
             }
        }
        else{
            throw new RuntimeException("wc: wrong arguments");
        }
        str_to_bytes.close();
    }
}
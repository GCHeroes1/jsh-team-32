
package uk.ac.ucl.jsh.shellprograms;

import java.io.*;

public class WC extends ShellProgram {
    private int countChar(Reader file) throws IOException {
        BufferedReader reader = new BufferedReader(file);
        //int charCount = 0;
        int charCount = reader.readLine().length();
        // String data = " ";
        // while (data != null) {
        //     try {
        //         data = reader.readLine();
        //     } catch (IOException e) {
                
        //         e.printStackTrace();
        //     }
        //     charCount += data.length();
        // }
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
        if (args.length < 1 || args.length > 2){
            throw new RuntimeException("wc: wrong number of arguments");
        }
        if (args[0].equals("-m")){
            if (args.length > 1){
                int char_ = countChar(new FileReader(args[1]));
                //int char_ = countChar(new InputStreamReader(stdin));
                String char_count = String.valueOf(char_);
                str_to_bytes.write(char_count); 
             }
            //  else if (args.length == 2){
            //     System.out.println("i got here 3 \n");
            //     str_to_bytes.write(countChar(new FileReader(args[1]))); 
            //   }
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
        //str_to_bytes.close();
    }
}
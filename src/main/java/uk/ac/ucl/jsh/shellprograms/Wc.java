package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Wc extends ShellProgram {
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
//        System.out.println(charCount);
//        System.out.println(wordCount);
//        System.out.println(lineCount);
        return new int[]{charCount, wordCount, lineCount};
        }

    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);

        // 0: default    1: -m    2: -w    3: -l
        int wc_option = 0; // by default, no option
        boolean use_stdin = false;
        int[] countArray = new int[]{0, 0, 0};

        // mode selection and determining whether or not to use stdin
        if (args.length > 0)
        {
            switch (args[0])
            {
                case "-m":
                    wc_option = 1;
                    break;
                case "-w":
                    wc_option = 2;
                    break;
                case "-l":
                    wc_option = 3;
                default:
//                    ArrayList<String> new_args = new ArrayList<>();
//                    for(String arg_entry : args)
//                    {
//
//                    }
                    break;
            }

            if(wc_option != 0 && args.length == 1)
            {
                use_stdin = true;
            }
        }
        else
        {
            use_stdin = true;
        }

        if(use_stdin)
        {
            InputStreamReader isr = new InputStreamReader(stdin);
            countArray = counting(isr);
        }
        else
        {
            Path filePath;
            BufferedReader reader;
            Charset encoding = StandardCharsets.UTF_8;
            for(String file : args)
            {
                if( !(file.equals("-w") | file.equals("-m") | file.equals("-l")) ) // todo:remove this when not necessary anymore
                {
                    filePath = Paths.get(currentDirectory + File.separator + file);

                    if (Files.notExists(filePath) || Files.isDirectory(filePath) ||
                            !Files.exists(filePath) || !Files.isReadable(filePath))
                    {
                        throw new RuntimeException("wc: wrong file argument");
                    }

                    reader = Files.newBufferedReader(filePath, encoding);
                    int[] currentCounting = counting(reader);
                    for (int j = 0; j < 3; j++) {
                        countArray[j] += currentCounting[j];
                    }
                }
            }
        }


        switch (wc_option)
        {
            case 0:
                str_to_bytes.write(String.valueOf(countArray[2]));
                str_to_bytes.write("\t");
                str_to_bytes.write(String.valueOf(countArray[1]));
                str_to_bytes.write("\t");
                str_to_bytes.write(String.valueOf(countArray[0]));
                break;
            case 1:
                String char_count = String.valueOf(countArray[0]);
                str_to_bytes.write(char_count);
                break;
            case 2:
                String word_count = String.valueOf(countArray[1]);
                str_to_bytes.write(word_count);
                break;
            case 3:
                String lines_count = String.valueOf(countArray[2]);
                str_to_bytes.write(lines_count);
                break;
        }

        str_to_bytes.write(System.getProperty("line.separator"));
        str_to_bytes.flush();
    }
}
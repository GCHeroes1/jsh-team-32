package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Wc extends ShellProgram
{
    private int[] counting(Reader file) throws IOException
    {
        BufferedReader reader = new BufferedReader(file);
        int charCount = 0;
        int wordCount = 0;
        int lineCount = 0;
        String line;
        while ((line = reader.readLine()) != null)
        {
            charCount += line.length() + 1;
            if (!(line.equals("")))
            {
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
        OutputStreamWriter strToBytes = new OutputStreamWriter(stdout);

        // 0: default    1: -m    2: -w    3: -l
        int wcOption = 0; // by default, no option
        boolean useStdin = false;
        int[] countArray = new int[]{0, 0, 0};

        // mode selection and determining whether or not to use stdin
        if (args.length > 0)
        {
            switch (args[0])
            {
                case "-m":
                    wcOption = 1;
                    break;
                case "-w":
                    wcOption = 2;
                    break;
                case "-l":
                    wcOption = 3;
                default:
                    break;
            }

            if (wcOption != 0 && args.length == 1)
            {
                useStdin = true;
            }
        } else
        {
            useStdin = true;
        }

        if (useStdin)
        {
            InputStreamReader isr = new InputStreamReader(stdin);
            countArray = counting(isr);
        } else
        {
            Path filePath;
            BufferedReader reader;
            Charset encoding = StandardCharsets.UTF_8;
            for (String file : args)
            {
                if (!(file.equals("-w") | file.equals("-m") | file.equals("-l"))) // todo:remove this when not necessary anymore
                {
                    filePath = Paths.get(currentDirectory + File.separator + file);
                    try
                    {
                        reader = Files.newBufferedReader(filePath, encoding);
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException("wc: cannot open file '" + filePath + "'");
                    }
                    int[] currentCounting = counting(reader);
                    for (int j = 0; j < 3; j++)
                    {
                        countArray[j] += currentCounting[j];
                    }
                }
            }
        }

        if(wcOption > 0)
        {
            strToBytes.write(String.valueOf(countArray[wcOption - 1]));
        }
        else
        {
            strToBytes.write(String.valueOf(countArray[2]));
            strToBytes.write("\t");
            strToBytes.write(String.valueOf(countArray[1]));
            strToBytes.write("\t");
            strToBytes.write(String.valueOf(countArray[0]));
        }


        strToBytes.write(System.getProperty("line.separator"));
        strToBytes.flush();
    }
}
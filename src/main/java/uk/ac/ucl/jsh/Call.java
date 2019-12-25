package uk.ac.ucl.jsh;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Call extends Jsh implements CommandInterface
{

    @Override
    public void run(String command, InputStream input, OutputStream output) throws IOException
    {
        command = cmd_sub(command);
        command = extract_io_redirects(command);

        ArrayList<String> tokens = split_quotes(command);

        {    // do command redirection here and let the IDE decide what best to do to replace input and output streams.
            //for (String token : tokens)
            for(int i = 0; i < tokens.size(); i++)
            {
                String token = tokens.get(i);
                String redirection_target;
                //System.out.println(token);
                switch (token.charAt(0))
                {
                    case '>':
                        redirection_target = get_redirection_target(tokens, i, token);

                        try
                        {
                            output = new FileOutputStream(new File(redirection_target));
                        }
                        catch (IOException e)
                        {
                            throw new RuntimeException("[redirection] Unable to open output file for writing");
                        }
                        break;


                    case '<':
                        redirection_target = get_redirection_target(tokens, i, token);

                        try
                        {
                            input = new FileInputStream(new File(redirection_target));
                        }
                        catch (IOException e)
                        {
                            throw new RuntimeException("[redirection] Unable to open input file for reading");
                        }
                        break;
                    default:
                }
            }
        }

        String appName = tokens.get(0); // first token = program to run
        ArrayList<String> appArgs = new ArrayList<>(tokens.subList(1, tokens.size()));
        try
        {
            spFactory.getSP(appName).execute(appArgs.toArray(new String[0]), input, output); //EHERERERERERE
        }
        catch (NullPointerException e)
        {
            throw new RuntimeException(appName + ": unknown application");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private String get_redirection_target(ArrayList<String> tokens, int i, String token) {
        String redirection_target;
        if(token.length() == 1)
        {
            try
            {
                redirection_target = tokens.get(i + 1);
                tokens.remove(i + 1);
                tokens.remove(i);
            }
            catch (IndexOutOfBoundsException e)
            {
                throw new RuntimeException("[redirection] No redirection target provided");
            }
        }
        else
        {
            redirection_target = token.substring(1);
            tokens.remove(token);
        }
        return redirection_target;
    }


    private String extract_io_redirects(String command)
    {
        return command;
    }

    private String cmd_sub(String command) throws IOException
    {
        int splitIndex, openingBackquoteIndex, closingBackquoteIndex;
        InputStream input = new ByteArrayInputStream(new byte[0]);
        String cmdoutput;
        for (splitIndex = 0; splitIndex < command.length(); splitIndex++)
        {                     // iterates through the command line characters
            char ch = command.charAt(splitIndex);                                               // isolates each character of the command line input
            if (ch == '`')
            {
                //String command = cmdline.substring(prevDelimiterIndex, splitIndex).trim();
                openingBackquoteIndex = command.indexOf(ch);
                closingBackquoteIndex = command.indexOf(ch, splitIndex + 1);
                ByteArrayOutputStream sub_command_output = new ByteArrayOutputStream();
                if (closingBackquoteIndex != -1)
                {
                    splitIndex = closingBackquoteIndex;
                    String subCommand = command.substring((openingBackquoteIndex + 1), closingBackquoteIndex); // create a command of the
                    (new Sequence()).run(subCommand, input, sub_command_output);
                    cmdoutput = (new String(sub_command_output.toByteArray()));
//                    while("\n\r".indexOf(cmdoutput.charAt(cmdoutput.length()-1)) != -1)  //removes trailing newlines
//                    {
//                        cmdoutput = cmdoutput.substring(0, cmdoutput.length()-2);
//                    }
                    cmdoutput = cmdoutput.replace("\n", " ").replace("\r", "").strip();
                    // System.out.println("pre: " + input);
                    command = command.substring(0, openingBackquoteIndex) + "\"" + cmdoutput + "\"" + command.substring(closingBackquoteIndex + 1);
                    splitIndex = openingBackquoteIndex + cmdoutput.length(); // +1 and not -2 because we added '"', '"' and ' '
                    // System.out.println("post: " + input);

                }
            }
        }
        return command;
    }

    private ArrayList<String> split_quotes(String command) throws IOException
    {
        String spaceRegex = "[^\\s\"']+|\"([^\"]*)\"|'([^']*)'";
        //String spaceRegex = "[^\\s\"'|]+([\\s]*\\|[\\s]*[^\\s\"'|]+)*|\"([^\"]*)\"|'([^']*)'";
        // regex above separates input into tokens by space and lonely single or double quotes, and keeps pipe characters in between words if surrounded by spaces or not. The pipe has to be between words.
        ArrayList<String> tokens = new ArrayList<>();                                       // know that whitespace \s is \\s in java and \| is \\| because we escape metacharacters
        Pattern regex = Pattern.compile(spaceRegex);                                        // just compiles the regex
        Matcher regexMatcher = regex.matcher(command);                                      // creates a "matcher"
        String nonQuote;
        while (regexMatcher.find()) {                                                       // as long as there is a match it will continue the while loop
            if (regexMatcher.group(1) != null || regexMatcher.group(2) != null) {           // check if is quoted
                String quoted = regexMatcher.group(0).trim();                               // group(0) is the entire thing, trims it?
                tokens.add(quoted.substring(1, quoted.length() - 1));                          // just removes the quotes
            } else {
                nonQuote = regexMatcher.group().trim();                                     // trims the entire regex
                ArrayList<String> globbingResult = new ArrayList<>();
                Path dir = Paths.get(currentDirectory);                                     // path object, represents operating system level directory
                DirectoryStream<Path> stream = null;     // using the OS to do globbing for him
                stream = Files.newDirectoryStream(dir, nonQuote);
                for (Path entry : stream) {
                    globbingResult.add(entry.getFileName().toString());                     // putting results back into the variable
                }
                if (globbingResult.isEmpty()) {
                    globbingResult.add(nonQuote);
                }
                tokens.addAll(globbingResult);
            }
        }
        return tokens;
    }
}

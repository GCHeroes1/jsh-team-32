package uk.ac.ucl.jsh;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pipe extends Jsh implements CommandInterface
{

    @Override
    public void run(String input, OutputStream output) throws IOException
    {
        //OutputStreamWriter temp_writer = new OutputStreamWriter(System.out);

        ArrayList<String> piped_cmds = new ArrayList<>();
        int closingPairIndex, prevDelimiterIndex = 0, splitIndex = 0, start_quote = 0;
        boolean inside_quote = false;
        for (splitIndex = 0; splitIndex < input.length(); splitIndex++) {                     // iterates through the command line characters
            char ch = input.charAt(splitIndex);                                               // isolates each character of the command line input
            if (ch == '\'' || ch == '\"')
            {                                                                                   // if it finds a quote (' or ")
                inside_quote = !inside_quote;
                if(inside_quote)
                {
                    start_quote = splitIndex;
                }
            }
            else if(ch == '|')
            {
                if(!inside_quote)
                {
                    piped_cmds.add(input.substring(start_quote, splitIndex));
                    piped_cmds.add(input.substring(splitIndex+1));
                }
            }
        }

        if(piped_cmds.isEmpty())
        {
            piped_cmds.add(input);
        }

//        String pipeRegex = "[^|]+";
//        Pattern piperegex = Pattern.compile(pipeRegex);
//        Matcher piperegexmatcher = piperegex.matcher(input);
//
//        while(piperegexmatcher.find())
//        {
//            piped_cmds.add(piperegexmatcher.group());
//        }


        for(String command : piped_cmds) {
            String spaceRegex = "[^\\s\"']+|\"([^\"]*)\"|'([^']*)'";
            //String spaceRegex = "[^\\s\"'|]+([\\s]*\\|[\\s]*[^\\s\"'|]+)*|\"([^\"]*)\"|'([^']*)'";
            // regex above separates input into tokens by space and lonely single or double quotes, and keeps pipe characters in between words if surrounded by spaces or not. The pipe has to be between words.
            ArrayList<String> tokens = new ArrayList<>();                                 // know that whitespace \s is \\s in java and \| is \\| because we escape metacharacters
            Pattern regex = Pattern.compile(spaceRegex);                                        // just compiles the regex
            Matcher regexMatcher = regex.matcher(command);                                   // creates a "matcher"
            String nonQuote;
            while (regexMatcher.find()) {                                                       // as long as there is a match it will continue the while loop
                if (regexMatcher.group(1) != null || regexMatcher.group(2) != null) {           // checking if there is a first and second group (it'd be null if it didn't exist?)
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
            String appName = tokens.get(0);                                                     // gets first token
            ArrayList<String> appArgs = new ArrayList<>(tokens.subList(1, tokens.size()));// creates a variable holding all the arguments for the program invoked
            // Here is the mess - does all the running the commands stuff
            try {
                System.out.println("running app " + appName + " with args " + appArgs);
                spFactory.getSP(appName).execute(appArgs.toArray(new String[0]), System.out); //EHERERERERERE
            } catch (NullPointerException e) {
                throw new RuntimeException(appName + ": unknown application");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}

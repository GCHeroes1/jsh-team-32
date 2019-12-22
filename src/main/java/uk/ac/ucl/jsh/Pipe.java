package uk.ac.ucl.jsh;


import java.io.*;
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
        int closingPairIndex, prevDelimiterIndex = 0, splitIndex = 0, start_quote = 0, last_pipe = 0;
        int openingBackquoteIndex, closingBackquoteIndex = 0;
        boolean inside_quote = false;
        String cmdoutput = "";
        for (splitIndex = 0; splitIndex < input.length(); splitIndex++) {                     // iterates through the command line characters
            char ch = input.charAt(splitIndex);                                               // isolates each character of the command line input
            if (ch == '`')
			{
				//String command = cmdline.substring(prevDelimiterIndex, splitIndex).trim();
				openingBackquoteIndex = input.indexOf(ch);
				closingBackquoteIndex = input.indexOf(ch, splitIndex + 1);
				if (closingBackquoteIndex != -1)
				{
					splitIndex = closingBackquoteIndex;
                    String subCommand = input.substring((openingBackquoteIndex+1), closingBackquoteIndex); // create a command of the 
                    Sequence sequence = new Sequence();
                    //subCommand = "\"" + subCommand + "\""; // bit hacky...
                    System.out.println("the sub command is " + subCommand); //its definitely getting the right argument for the next sequence call
                    //THIS IS TO BE CHANGED - NEED TO USE BYTE ARRAY IDK - not necessary?
                    // First call sequence, skip over the ` (doesnt actually seem to work but not sure), then call pipe with the contents of `...` as the argument 
                    // In pipe, check for `, in this case, run sequence with the subset of commands that were within `
                    //cmdoutput = "foo bar";
                    // my logic is flawed, the result of the next sequence call is the thing that needs quotes around it 
                    sequence.run(subCommand, output);
				}
			}
            else if (ch == '\'' || ch == '\"')
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
                    piped_cmds.add(input.substring(last_pipe, splitIndex));
                    last_pipe = splitIndex + 1;
                    //piped_cmds.add(input.substring(splitIndex+1));
                }
            }
        }

        piped_cmds.add(input.substring(last_pipe, splitIndex));

//        String pipeRegex = "[^|]+";
//        Pattern piperegex = Pattern.compile(pipeRegex);
//        Matcher piperegexmatcher = piperegex.matcher(input);
//
//        while(piperegexmatcher.find())
//        {
//            piped_cmds.add(piperegexmatcher.group());
//        }


        //InputStream instream = new ByteArrayInputStream(piped_cmds.get(0).getBytes());
//        ByteArrayInputStream outputStream = new ByteArrayInputStream();
        //OutputStreamWriter hellothere = new OutputStreamWriter(outstream);
        for(String command : piped_cmds) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[2048]);
            String spaceRegex = "[^\\s\"']+|\"([^\"]*)\"|'([^']*)'";
            //String spaceRegex = "[^\\s\"'|]+([\\s]*\\|[\\s]*[^\\s\"'|]+)*|\"([^\"]*)\"|'([^']*)'";
            // regex above separates input into tokens by space and lonely single or double quotes, and keeps pipe characters in between words if surrounded by spaces or not. The pipe has to be between words.
            ArrayList<String> tokens = new ArrayList<>();                                       // know that whitespace \s is \\s in java and \| is \\| because we escape metacharacters
            Pattern regex = Pattern.compile(spaceRegex);                                        // just compiles the regex
            Matcher regexMatcher = regex.matcher(command);                                      // creates a "matcher"
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


            try
            {
                inputStream = new ByteArrayInputStream(cmdoutput.getBytes());
                //System.out.println("running app " + appName + " with args " + appArgs + " and stdin " + inputStream);
                //appArgs.toArray(new String[0])
                spFactory.getSP(appName).execute(appArgs.toArray(new String[0]), inputStream, outputStream); //EHERERERERERE
                cmdoutput = (new String(outputStream.toByteArray()));
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
        OutputStreamWriter osw = new OutputStreamWriter(System.out);
        osw.write(cmdoutput);
        osw.flush();
        //osw.close();



    }

}


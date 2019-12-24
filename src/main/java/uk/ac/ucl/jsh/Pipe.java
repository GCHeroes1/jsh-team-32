package uk.ac.ucl.jsh;


import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pipe extends Jsh implements CommandInterface
{

    @Override
    public void run(String command, InputStream input, OutputStream output) throws IOException
    {
        //OutputStreamWriter temp_writer = new OutputStreamWriter(System.out);

        ArrayList<String> piped_cmds = new ArrayList<>();
        int closingPairIndex, prevDelimiterIndex = 0, splitIndex = 0, start_quote = 0, last_pipe = 0;
        int openingBackquoteIndex, closingBackquoteIndex = 0;
        boolean inside_quote = false;
        String cmdoutput = "";
        for (splitIndex = 0; splitIndex < command.length(); splitIndex++) {                     // iterates through the command line characters
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
                    String subCommand = command.substring((openingBackquoteIndex+1), closingBackquoteIndex); // create a command of the
                    (new Sequence()).run(subCommand, input,sub_command_output);
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
            else if (ch == '\'' || ch == '\"') // if it finds a quote (' or ")
            {
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
                    piped_cmds.add(command.substring(last_pipe, splitIndex));
                    last_pipe = splitIndex + 1;
                    //piped_cmds.add(input.substring(splitIndex+1));
                }
            }
        }

        piped_cmds.add(command.substring(last_pipe, splitIndex));

//        String pipeRegex = "[^|]+";
//        Pattern piperegex = Pattern.compile(pipeRegex);
//        Matcher piperegexmatcher = piperegex.matcher(input);
//
//        while(piperegexmatcher.find())
//        {
//            piped_cmds.add(piperegexmatcher.group());
//        }


        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        ByteArrayInputStream instream;
        for(String cmd : piped_cmds) {
            instream = new ByteArrayInputStream(outstream.toByteArray());

            cmd = merge_collated_quotes(cmd);

            (new Call()).run(cmd, instream, outstream);
        }
        OutputStreamWriter osw = new OutputStreamWriter(output);
        osw.write(outstream.toString());
        osw.flush();
    }

    private String merge_collated_quotes(String command)
    {
        String collated_quotes_regex = "([^\\s\"]*(\"[^\"]*\")*[^\\s\"]*)|([^\\s\']*(\'[^\']*\')*[^\\s\']*)";
        Pattern regex_pattern = Pattern.compile(collated_quotes_regex);
        Matcher regex_matcher = regex_pattern.matcher(command);
        ArrayList<String> pieces = new ArrayList<>();
        String match;
        while (regex_matcher.find())
        {
            match = regex_matcher.group();
            if(match.indexOf('"') != -1)  // only put quotes around if the part contains quotes
            {
                match = match.replace("\"", "");
                match = "\"" + match + "\"";
            }
            pieces.add(match);
        }
        return String.join(" ", pieces);
    }



}


package uk.ac.ucl.jsh;


import java.io.*;
import java.util.ArrayList;

public class Pipe extends Jsh implements CommandInterface
{

    @Override
    public void run(String command, InputStream input, OutputStream output) throws IOException
    {
        ArrayList<String> piped_commands = split_piped_commands(command);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        ByteArrayInputStream instream;
        for(String cmd : piped_commands) {
            // get the output of previous command as input stream for next command.
            instream = new ByteArrayInputStream(outstream.toByteArray());
            outstream.reset();

            //cmd = merge_collated_quotes(cmd);

            (new Call()).run(cmd, instream, outstream);
        }
        OutputStreamWriter osw = new OutputStreamWriter(output);
        osw.write(outstream.toString());
        osw.flush();
    }



    private ArrayList<String> split_piped_commands(String command)
    {
        ArrayList<String> commands = new ArrayList<>();
        int splitIndex, last_pipe = 0;
        boolean inside_quote = false;
        for (splitIndex = 0; splitIndex < command.length(); splitIndex++) {                     // iterates through the command line characters
            char ch = command.charAt(splitIndex);                                               // isolates each character of the command line input
            if (ch == '\'' || ch == '\"') // if it finds a quote (' or ")
            {
                inside_quote = !inside_quote;
            } else if ((ch == '|') && !inside_quote) {
                commands.add(command.substring(last_pipe, splitIndex).trim());
                last_pipe = splitIndex + 1;
            }
        }
        commands.add(command.substring(last_pipe, splitIndex).trim());
        return commands;
    }



}


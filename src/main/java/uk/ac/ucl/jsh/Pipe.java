package uk.ac.ucl.jsh;


import java.io.*;
import java.util.ArrayList;

public class Pipe extends Jsh implements CommandInterface
{

    @Override
    public void run(String command, InputStream input, OutputStream output) throws IOException
    {
        ArrayList<String> pipedCommands = splitPipedCommands(command);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ByteArrayInputStream inStream;
        for (String cmd : pipedCommands)
        {
            // get the output of previous command as input stream for next command.
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            outStream.reset();

            (new Call()).run(cmd, inStream, outStream);
        }
        OutputStreamWriter osw = new OutputStreamWriter(output);
        osw.write(outStream.toString());
        osw.flush();
    }


    private ArrayList<String> splitPipedCommands(String command)
    {
        ArrayList<String> commands = new ArrayList<>();
        int splitIndex, lastPipe = 0;
        boolean insideQuote = false;
        for (splitIndex = 0; splitIndex < command.length(); splitIndex++)
        {                     // iterates through the command line characters
            char ch = command.charAt(splitIndex);                                               // isolates each character of the command line input
            if (ch == '\'' || ch == '\"' || ch == '`') // if it finds a quote (' or ")
            {
                insideQuote = !insideQuote;
            } else if (ch == '|' && !insideQuote)
            {
                commands.add(command.substring(lastPipe, splitIndex).trim());
                lastPipe = splitIndex + 1;
            }
        }
        commands.add(command.substring(lastPipe, splitIndex).trim());
        return commands;
    }


}


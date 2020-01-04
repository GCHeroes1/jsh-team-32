package uk.ac.ucl.jsh;

import org.apache.commons.io.output.NullOutputStream;
import uk.ac.ucl.jsh.shellprograms.ShellProgram;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Call extends Jsh implements CommandInterface
{
    private InputStream input;
    private OutputStream output;

    @Override
    public void run(String command, InputStream input, OutputStream output) throws IOException
    {
        command = cmd_sub(command); //execute command substitution
        ArrayList<String> tokens = split_to_tokens(command); //globbing happens inside split_quotes

        this.input = input;
        this.output = output;
        //DO IO REDIRECTION HERE
        io_redirection(tokens);
        input = this.input;
        output = this.output;


        ArrayList<String> new_tokens = new ArrayList<>();
        for (String c : tokens)
        {
            new_tokens.addAll(glob(c));
        }
        tokens = new_tokens;

        new_tokens = new ArrayList<>();
        for (String c : tokens)
        {
            new_tokens.add(strip_quotes(c));
        }
        tokens = new_tokens;


        boolean unsafe = false;
        ShellProgram program;
        String appName = tokens.get(0); // first token = program to run
        ArrayList<String> appArgs = new ArrayList<>(tokens.subList(1, tokens.size()));
        if(appName.charAt(0) == '_')
        {
            unsafe = true;
            appName = appName.substring(1);
        }

        program = spFactory.getSP(appName);

        if (unsafe) program.executeUnsafe(appArgs.toArray(new String[0]), input, output);
        else program.execute(appArgs.toArray(new String[0]), input, output);
    }

    private void io_redirection(ArrayList<String> tokens) throws FileNotFoundException
    {
        boolean in = false, out = false;
        for (int index_of_token = 0; index_of_token < tokens.size(); index_of_token++)
        {
            String token = tokens.get(index_of_token);
            String redirection_target;

            switch (token.charAt(0))
            {
                case '>':
                    if(out) throw new RuntimeException("[IO Redirection] Multiple output targets specified");
                    redirection_target = get_redirection_target(tokens, index_of_token, token);
                    if(redirection_target.equals(""))
                    {
                        this.output = OutputStream.nullOutputStream();
                    }
                    else
                    {
                        this.output = new FileOutputStream(new File(currentDirectory + File.separator + redirection_target));
                    }
                    index_of_token = 0;
                    out = true;
                    break;

                case '<':
                    if(in) throw new RuntimeException("[IO Redirection] Multiple input sources specified");
                    redirection_target = get_redirection_target(tokens, index_of_token, token);
                    if(redirection_target.equals(""))
                    {
                        this.input = new ByteArrayInputStream(new byte[0]);
                    }
                    else
                    {
                        this.input = new FileInputStream(new File(currentDirectory + File.separator + redirection_target));
                    }
                    index_of_token = 0;
                    in = true;
                    break;
            }
        }
    }

    private String get_redirection_target(ArrayList<String> tokens, int index_of_token, String token)
    {
        String redirection_target;
        if (token.length() == 1)
        {
            try
            {
                redirection_target = tokens.get(index_of_token + 1);
                if("<>".contains(Character.toString(redirection_target.charAt(0))))
                {
                    redirection_target = "";
                }
                else
                {
                    tokens.remove(index_of_token + 1);
                }
                tokens.remove(index_of_token);
            }
            catch (IndexOutOfBoundsException e)
            {
                redirection_target = "";
                tokens.remove(index_of_token);
            }
        }
        else
        {
            redirection_target = token.substring(1);
            tokens.remove(token);
        }
        return redirection_target;
    }


    private boolean is_quote_not_disabled(String command, int index_of_quote)
    {
        return is_quote_not_disabled(command, index_of_quote, false);
    }

    private boolean is_quote_not_disabled(String command, int index_of_quote, boolean ignore_backticks)
    {
        char chr = command.charAt(index_of_quote);

        if (command.substring(0, index_of_quote).lastIndexOf('`') != -1 &&
                command.substring(index_of_quote + 1).indexOf('`') != -1 &&
                !ignore_backticks)
        {
            return false;
        }

        switch (chr)
        {
            case '"':
            case '`':
                return is_char_not_surrounded_by(command, index_of_quote, '\'');
            case '\'':
                return is_char_not_surrounded_by(command, index_of_quote, '"');
            default:
                return is_char_not_surrounded_by(command, index_of_quote, '"') &&
                        is_char_not_surrounded_by(command, index_of_quote, '\'');
        }

    }


    private boolean is_char_not_surrounded_by(String command, int index_of_quote, char quote_to_check)
    {
        boolean inside_quote = false;
        int index;

        for (index = 0; index <= index_of_quote; index++)
        {
            if (quote_to_check == command.charAt(index)) //if char at index is one of ", ', or `
            {
                inside_quote = !inside_quote;
            }
        }
        return !inside_quote;
    }


    //this method was originally written to be called from somewhere else
    //which is why there are some checks that are irrelevant now
    private String cmd_sub(String command) throws IOException
    {
        int splitIndex, openingBackquoteIndex, closingBackquoteIndex;
        InputStream input = new ByteArrayInputStream(new byte[0]);
        String cmdoutput;
        for (splitIndex = 0; splitIndex < command.length(); splitIndex++)
        {                     // iterates through the command line characters
            char ch = command.charAt(splitIndex);                                               // isolates each character of the command line input
            if (ch == '`' && is_quote_not_disabled(command, splitIndex))
            {
                //String command = cmdline.substring(prevDelimiterIndex, splitIndex).trim();
                openingBackquoteIndex = command.indexOf(ch);
                closingBackquoteIndex = command.indexOf(ch, splitIndex + 1);
                if (closingBackquoteIndex != -1) // check if quote is closed
                {
                    ByteArrayOutputStream sub_command_output = new ByteArrayOutputStream();
                    String subCommand = command.substring(openingBackquoteIndex + 1, closingBackquoteIndex); // create a command of the
                    (new Sequence()).run(subCommand, input, sub_command_output);
                    cmdoutput = new String(sub_command_output.toByteArray());
                    cmdoutput = cmdoutput.replace("\n", " ").replace("\r", "").strip();


                    command = command.substring(0, openingBackquoteIndex) + "`" + cmdoutput + "`" + command.substring(closingBackquoteIndex + 1);
                    splitIndex = openingBackquoteIndex + cmdoutput.length();

                }
            }
        }
        return command;
    }

    private ArrayList<String> split_to_tokens(String command)
    {
        ArrayList<String> tokens = new ArrayList<>();

        int last_quote_end = 0;
        for (int scanning_index = 0; scanning_index < command.length(); scanning_index++)
        {
            char chr = command.charAt(scanning_index);
            if (chr == ' ' && is_quote_not_disabled(command, scanning_index, true))
            {
                tokens.add(command.substring(last_quote_end, scanning_index));
                last_quote_end = scanning_index + 1;
            }
            else if (scanning_index == command.length() - 1)
            {
                tokens.add(command.substring(last_quote_end));
            }

        }
        while(tokens.contains(""))
        {
            tokens.remove("");
        }
        return tokens;
    }

    private String strip_quotes(String command)
    {
        int index;
        StringBuilder stripped = new StringBuilder();
        char chr;
        for (index = 0; index < command.length(); index++)
        {
            chr = command.charAt(index);
            if ("\"'`".indexOf(chr) == -1 || !is_quote_not_disabled(command, index))
            {
                stripped.append(chr);
            }
        }
        return stripped.toString();
    }

    private ArrayList<String> glob(String glob_string) throws IOException
    {
        boolean is_absolute;
        ArrayList<String> glob_matches = new ArrayList<>();
        File glob = new File(glob_string);
        if (!(is_absolute = glob.isAbsolute()))
        {
            glob = new File(currentDirectory + File.separator + glob_string);
        }

        if (glob.isDirectory() && !is_absolute)
        {
            glob_matches.add(Paths.get(glob_string).toString().replace(currentDirectory, ""));
            return glob_matches;
        }

        File parent_file;
        Path glob_working_dir = Paths.get(currentDirectory);
        if ((parent_file = glob.getParentFile()) != null && parent_file.isDirectory())
        {
            glob_working_dir = parent_file.toPath();
        }


        DirectoryStream<Path> stream;
        stream = Files.newDirectoryStream(glob_working_dir, glob.getName());
        String rel_path = Paths.get(currentDirectory).relativize(glob_working_dir).toString();
        for (Path entry : stream)
        {
            if (rel_path.equals("")) //empty path means both are equal
            {
                glob_matches.add(entry.getFileName().toString());
            }
            else if (!is_absolute)
            {
                glob_matches.add(rel_path + File.separator + entry.getFileName().toString());
            }
        }
        if (glob_matches.isEmpty())
        {
            glob_matches.add(glob_string);
        }
        return glob_matches;
    }
}

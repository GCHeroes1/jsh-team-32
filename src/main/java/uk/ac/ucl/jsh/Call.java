package uk.ac.ucl.jsh;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Call extends Jsh implements CommandInterface
{

    @Override
    public void run(String command, InputStream input, OutputStream output) throws IOException
    {
        command = cmd_sub(command);
        command = extract_io_redirects(command);
        //command = merge_collated_quotes(command);

        //globbing happens inside split_quotes
        ArrayList<String> tokens = split_to_tokens(command);

        //execute io redirection
        {
            for (int i = 0; i < tokens.size(); i++)
            {
                String token = tokens.get(i);
                String redirection_target;

                switch (token.charAt(0))
                {
                    case '>':
                        redirection_target = get_redirection_target(tokens, i, token);
                        try
                        {
                            output = new FileOutputStream(new File(currentDirectory + File.separator + redirection_target));
                        } catch (IOException e)
                        {
                            throw new RuntimeException("[redirection] Unable to open output file for writing");
                        }
                        break;


                    case '<':
                        redirection_target = get_redirection_target(tokens, i, token);
                        try
                        {
                            input = new FileInputStream(new File(currentDirectory + File.separator + redirection_target));
                        } catch (IOException e)
                        {
                            throw new RuntimeException("[redirection] Unable to open input file for reading");
                        }
                        break;
                    default:
                }
            }
        }

        ArrayList<String> newtokens = new ArrayList<>();
        for(String c : tokens)
        {
            newtokens.addAll(glob(c));
        }
        tokens = newtokens;

        newtokens = new ArrayList<>();
        for(String c : tokens)
        {
            newtokens.add(strip_quotes(c));
        }
        tokens = newtokens;





        //do command substitution
//        ArrayList<String> new_list = new ArrayList<>();
//        for (String token_string : tokens)
//        {
//            new_list.addAll(split_spaces(cmd_sub(token_string)));
//        }
//        tokens = new_list;


        String appName = tokens.get(0); // first token = program to run
        ArrayList<String> appArgs = new ArrayList<>(tokens.subList(1, tokens.size()));
        try
        {
            if (appName.charAt(0) == '_')
            {
                spFactory.getSP(appName.substring(1)).executeUnsafe(appArgs.toArray(new String[0]), input, output);
            }
            else
            {
                spFactory.getSP(appName).execute(appArgs.toArray(new String[0]), input, output);
            }
        } catch (NullPointerException e)
        {
            throw new RuntimeException(appName + ": unknown application");
        }

    }

    private String get_redirection_target(ArrayList<String> tokens, int i, String token)
    {
        String redirection_target;
        if (token.length() == 1)
        {
            try
            {
                redirection_target = tokens.get(i + 1);
                tokens.remove(i + 1);
                tokens.remove(i);
            } catch (IndexOutOfBoundsException e)
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

//    private boolean is_char_disabled_by_quotes(String command, int index_of_char)
//    {
//        return !is_quote_not_disabled(command, index_of_char);
//    }

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
                return !is_char_surrounded_by(command, index_of_quote, '\'');
            case '\'':
                return !is_char_surrounded_by(command, index_of_quote, '"');
            default:
                return !is_char_surrounded_by(command, index_of_quote, '"') &&
                        !is_char_surrounded_by(command, index_of_quote, '\'');
        }

    }


    private boolean is_char_surrounded_by(String command, int index_of_quote, char quote_to_check)
    {
        boolean inside_quote = false;
//        int quote_start;
//        int quote_end;
        int index;

        for (index = 0; index < command.length(); index++)
        {
            if(index == index_of_quote)
            {
                return inside_quote;
            }
            if(quote_to_check == command.charAt(index)) //if char at index is one of ", ', or `
            {
//                if(!inside_quote)
//                {
//                    quote_start = index;
//                }
//                else
//                {
//                    quote_end = index;
//                }
                inside_quote = !inside_quote;
            }
        }
        return false;

//        if ((quote_start = command.indexOf(quote_to_check)) == -1)
//        {
//            return true;
//        }
//        else
//        {
//            quote_end = command.substring(quote_start + 1).indexOf(quote_to_check) + quote_start + 1;
//            return quote_start >= index_of_quote || index_of_quote >= quote_end;
//        }
    }

//    private String merge_collated_quotes(String command)
//    {
//        int space_end = 0, q_end = -1, q_index;
//        for (q_index = 0; q_index < command.length(); q_index++)
//        {
//            char chr = command.charAt(q_index);
//            if (chr == '"' || chr == '\'')
//            {
//                if (is_quote_not_disabled(command, q_index) && q_index > 0)
//                {
//                    if (q_index == command.length() - 1)
//                    {
//                        continue;
//                    }
//                    else if (command.charAt(q_index + 1) == ' ')
//                    {
//                        continue;
//                    }
//
//                    if (command.charAt(q_index - 1) != ' ' && space_end > q_end)
//                    {
//                        command = command.substring(0, space_end + 1) + chr +
//                                command.substring(space_end + 1, q_index) +
//                                command.substring(q_index + 1);
//                        q_end = space_end;
//                    }
//                    else if (space_end == q_end)
//                    {
//                        q_end = q_index;
//                    }
//                    else if (q_end > space_end && command.charAt(q_end) == chr)
//                    { // check if they're the same kind of quotes, otherwise ignore
//                        command = command.substring(0, q_end) +
//                                command.substring(q_end + 1, q_index) +
//                                command.substring(q_index + 1);
//                        q_index = q_index - 2;
//                        q_end = space_end;
//                    }
//                }
//            }
//            else if (chr == ' ')
//            {
//                if (q_end > space_end)
//                {
//                    command = command.substring(0, q_end) +
//                            command.substring(q_end + 1, q_index) +
//                            command.charAt(q_end);
//                }
//                space_end = q_index;
//            }
//            else if (q_index == command.length() - 1 && q_end > space_end)
//            {
//                command = command.substring(0, q_end) +
//                        command.substring(q_end + 1) +
//                        command.charAt(q_end);
//            }
//        }
//        return command;
//    }

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

//    private ArrayList<String> split_spaces(String command)
//    {
//        ArrayList<String> temp_list = new ArrayList<>();
//        int last_split = 0;
//        for (int i = 0; i < command.length(); i++)
//        {
//            char chr = command.charAt(i);
//            if (chr == ' ' && is_quote_not_disabled(command, i))
//            {
//                temp_list.add(command.substring(last_split, i));
//                last_split = i + 1;
//            }
//        }
//        temp_list.add(command.substring(last_split));
//        return temp_list;
//    }

    private ArrayList<String> split_to_tokens(String command) throws IOException
    {
        ArrayList<String> tokens = new ArrayList<>();

        int last_quote_end = 0;
        for (int scanning_index = 0; scanning_index < command.length(); scanning_index++)
        {
            char chr = command.charAt(scanning_index);
//            if (chr == '"' && is_quote_not_disabled(command, quote_scanning_index))
//            {
//                quote_start = quote_scanning_index;
//                if ((quote_end = command.indexOf("\"", quote_start + 1)) != -1)
//                {
//                    while (!is_quote_not_disabled(command, quote_end))
//                    {
//                        quote_end = command.indexOf("\"", quote_end + 1);
//                        if (quote_end == -1)
//                        {
//                            throw new RuntimeException("unmatched quote");
//                        }
//                    }
//                    tokens.add(command.substring(quote_start + 1, quote_end));
//
//                }
//            }
//            else if (chr == '\'' && is_quote_not_disabled(command, quote_scanning_index))
//            {
//                quote_start = quote_scanning_index;
//                if ((quote_end = command.indexOf("\'", quote_start + 1)) != -1)
//                {
//                    while (!is_quote_not_disabled(command, quote_end))
//                    {
//                        quote_end = command.indexOf("\'", quote_end + 1);
//                        if (quote_end == -1)
//                        {
//                            throw new RuntimeException("unmatched quote");
//                        }
//                    }
//                    tokens.add(command.substring(quote_start + 1, quote_end));
//                }
//            }
//            else if (chr == ' ' && is_quote_not_disabled(command, quote_scanning_index))
//            {
//                if (quote_end != -1 && quote_end < quote_scanning_index)
//                {
//                    String token = command.substring(quote_end, quote_scanning_index);
//                    tokens.addAll(glob(token));
//                }
//                quote_end = quote_scanning_index + 1;
//            }
//            else if (quote_scanning_index == command.length() - 1)
//            {
//                String token = command.substring(quote_end);
//                tokens.addAll(glob(token));
//            }
            if(chr == ' ' && is_quote_not_disabled(command, scanning_index, true))
            {
                tokens.add(command.substring(last_quote_end, scanning_index));
                last_quote_end = scanning_index + 1;
            }
            else if (scanning_index == command.length() - 1)
            {
                tokens.add(command.substring(last_quote_end));
            }

        }
        tokens.remove("");
//
//        String spaceRegex = "[^\\s\"`']+|\"([^\"]*)\"|'([^']*)'|`([^`]*)`";
//        //String spaceRegex = "[^\\s\"'|]+([\\s]*\\|[\\s]*[^\\s\"'|]+)*|\"([^\"]*)\"|'([^']*)'";
//        // regex above separates input into tokens by space and lonely single or double quotes, and keeps pipe characters in between words if surrounded by spaces or not. The pipe has to be between words.
//        Pattern regex = Pattern.compile(spaceRegex);                                        // just compiles the regex
//        Matcher regexMatcher = regex.matcher(command);                                      // creates a "matcher"
//        String nonQuote;
//        while (regexMatcher.find()) {                                                       // as long as there is a match it will continue the while loop
//            if (regexMatcher.group(1) != null || regexMatcher.group(2) != null) {           // check if is quoted
//                String quoted = regexMatcher.group(0).trim();                               // group(0) is the entire thing, trims it?
//                tokens.add(quoted.substring(1, quoted.length() - 1));                          // just removes the quotes
//            } else {
//                nonQuote = regexMatcher.group().trim();                                     // trims the entire regex
//                ArrayList<String> globbingResult = new ArrayList<>();
//                File glob = new File(currentDirectory + File.separator + nonQuote);
//                Path dir = Paths.get(currentDirectory);                                     // path object, represents operating system level directory
//                if(glob.getParentFile().isDirectory())
//                {
//                    dir = glob.getParentFile().toPath();
//                }
//                DirectoryStream<Path> stream = null;     // using the OS to do globbing for him
//                stream = Files.newDirectoryStream(dir, glob.getName());
//                String rel_path = Paths.get(currentDirectory).relativize(dir).toString();
//                for (Path entry : stream) {
//                    if(rel_path.equals(""))
//                    {
//                        globbingResult.add(entry.getFileName().toString());                     // putting results back into the variable
//                    }
//                    else
//                    {
//                        globbingResult.add(rel_path + File.separator + entry.getFileName().toString());
//                    }
//                }
//                if (globbingResult.isEmpty()) {
//                    globbingResult.add(nonQuote);
//                }
//                tokens.addAll(globbingResult);
//            }
//        }
        return tokens;
    }

    private String strip_quotes(String command)
    {
        int index;
        //ArrayList<Integer> to_remove = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        char chr;
        for(index = 0; index < command.length(); index++)
        {
            chr = command.charAt(index);
            if("\"'`".indexOf(chr) == -1 || !is_quote_not_disabled(command, index))
            {
                sb.append(chr);
            }
        }

        return sb.toString();
    }

    private ArrayList<String> glob(String glob_string) throws IOException
    {
        ArrayList<String> glob_matches = new ArrayList<>();
        File glob;
        if (glob_string.charAt(0) == '/')
        {
            glob = new File(glob_string);
        }
        else
        {
            glob = new File(currentDirectory + File.separator + glob_string);
        }
        Path dir = Paths.get(currentDirectory);

        File parent_file;
        if ((parent_file = glob.getParentFile()) != null && parent_file.isDirectory())
        {
            dir = parent_file.toPath();
        }
        DirectoryStream<Path> stream;
        stream = Files.newDirectoryStream(dir, glob.getName());
        String rel_path = Paths.get(currentDirectory).relativize(dir).toString();
        for (Path entry : stream)
        {
            if (rel_path.equals(""))
            {
                glob_matches.add(entry.getFileName().toString());
            }
            else
            {
                glob_matches.add(rel_path + File.separator + entry.getFileName().toString());
            }
        }
        if (glob_matches.isEmpty())
        {
            glob_matches.add(glob_string);
        }
        return new ArrayList<>(glob_matches);
    }
}

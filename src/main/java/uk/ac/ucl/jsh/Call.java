package uk.ac.ucl.jsh;

import java.io.*;
import java.util.ArrayList;

public class Call extends Jsh implements CommandInterface
{

    @Override
    public void run(String command, InputStream input, OutputStream output) throws IOException
    {
        //command = cmd_sub(command);
        command = extract_io_redirects(command);
        command = merge_collated_quotes(command);

        ArrayList<String> tokens = split_quotes(command);

        {
            for(int i = 0; i < tokens.size(); i++)
            {
                String token = tokens.get(i);
                String redirection_target;

                if(token.length() == 0)
                {
                    continue;
                }

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

        ArrayList<String> new_list = new ArrayList<>(tokens.size());
        for(String token_string : tokens)
        {
            new_list.addAll(split_spaces(cmd_sub(token_string)));
        }
        tokens = new_list;

        String appName = tokens.get(0); // first token = program to run
        ArrayList<String> appArgs = new ArrayList<>(tokens.subList(1, tokens.size()));
        try
        {
            if(appName.charAt(0) == '_')
            {
                spFactory.getSP(appName.substring(1)).executeUnsafe(appArgs.toArray(new String[0]), input, output);
            }
            else
            {
                spFactory.getSP(appName).execute(appArgs.toArray(new String[0]), input, output);
            }
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

    private boolean is_quote_not_disabled(String command, int index_of_quote)
    {
        char chr = command.charAt(index_of_quote);
        int quote_start, quote_end;

        if(command.substring(0, index_of_quote).lastIndexOf('`') != -1 &&
                command.substring(index_of_quote + 1).indexOf('`') != -1)
        {
            return false;
        }

        switch(chr)
        {
            case '"': //if we're checking if a double quote is disabled, we look for single quotes
                if((quote_start = command.indexOf('\'')) == -1)
                {
                    return true;
                }
                else
                {
                    quote_end = command.substring(quote_start + 1).indexOf('\'') + quote_start + 1;
                    if(quote_start < index_of_quote && index_of_quote < quote_end)
                    {
                        return false;
                    }
                }
                break;
            case '\'':
                if((quote_start = command.indexOf('"')) == -1)
                {
                    return true;
                }
                else
                {
                    quote_end = command.substring(quote_start + 1).indexOf('"')  + quote_start + 1;
                    if(quote_start < index_of_quote && index_of_quote < quote_end)
                    {
                        return false;
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    private String merge_collated_quotes(String command)
    {
        int space_end = 0, q_end = -1, q_index;
        for(q_index = 0; q_index < command.length(); q_index++)
        {
            char chr = command.charAt(q_index);
            if(chr == '"' || chr == '\'')
            {
                if(is_quote_not_disabled(command, q_index)) {
                    if (q_index > 0) {
                        if (q_index == command.length() - 1) {
                            continue;
                        } else if (command.charAt(q_index + 1) == ' ') {
                            continue;
                        }

                        if (command.charAt(q_index - 1) != ' ' && space_end > q_end) {
                            command = command.substring(0, space_end + 1) + chr +
                                    command.substring(space_end + 1, q_index) +
                                    command.substring(q_index + 1);
                            q_end = space_end;
                        } else if (space_end == q_end) {
                            q_end = q_index;
                        } else if (q_end > space_end) {
                            if (command.charAt(q_end) == chr) // check if they're the same kind of quotes, otherwise ignore

                            {
                                command = command.substring(0, q_end) +
                                        command.substring(q_end + 1, q_index) +
                                        command.substring(q_index + 1);
                                q_index = q_index - 2;
                                q_end = space_end;
                            }
                        }
                    }
                }
            }
            else if(chr == ' ')
            {
                if(q_end > space_end)
                {
                    command = command.substring(0, q_end) +
                            command.substring(q_end + 1, q_index) +
                            command.charAt(q_end);
                }
                space_end = q_index;
            }
            else if(q_index == command.length() - 1 && q_end > space_end)
            {
                command = command.substring(0, q_end) +
                        command.substring(q_end + 1) +
                        command.charAt(q_end);
            }
        }

//        String collated_quotes_regex = "([^\\s\"]*(\"[^\"]*\")+[^\\s\"]*)|([^\\s\']*(\'[^\']*\')+[^\\s\']*)|[^\"']+";
//        Pattern regex_pattern = Pattern.compile(collated_quotes_regex);
//        //command = command.replace("\'", "\"");
//        Matcher regex_matcher = regex_pattern.matcher(command);
//        ArrayList<String> pieces = new ArrayList<>();
//        String match;
//
//        while (regex_matcher.find())
//        {
//            match = regex_matcher.group();
//            int q_index;
//            if((q_index = match.indexOf('\'')) != -1 && is_quote_not_disabled(match, q_index))  // only put quotes around if the part contains quotes
//            {
//                match = match.replace("\'", "");
//                match = "\'" + match + "\'";
//            }
//            if((q_index = match.indexOf('\"')) != -1 && is_quote_not_disabled(match, q_index))  // only put quotes around if the part contains quotes
//            {
//                match = match.replace("\"", "");
//                match = "\"" + match + "\"";
//            }
//
//            pieces.add(match);
//        }
        //return String.join(" ", pieces);
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
                if (closingBackquoteIndex != -1) // check if quote is closed
                {
                    ByteArrayOutputStream sub_command_output = new ByteArrayOutputStream();
                    String subCommand = command.substring((openingBackquoteIndex + 1), closingBackquoteIndex); // create a command of the
                    (new Sequence()).run(subCommand, input, sub_command_output);
                    cmdoutput = (new String(sub_command_output.toByteArray()));
                    cmdoutput = cmdoutput.replace("\n", " ").replace("\r", "").strip();


                    command = command.substring(0, openingBackquoteIndex) + cmdoutput + command.substring(closingBackquoteIndex + 1);
                    splitIndex = openingBackquoteIndex + cmdoutput.length() - 2;

                }
            }
        }
        return command;
    }

    private ArrayList<String> split_spaces(String command)
    {
        ArrayList<String> temp_list = new ArrayList<>();
        int last_split = 0;
        for (int i = 0; i < command.length(); i++)
        {
            char chr = command.charAt(i);
            if(chr == ' ' && is_quote_not_disabled(command, i))
            {
                temp_list.add(command.substring(last_split, i));
                last_split = i + 1;
            }
        }
        temp_list.add(command.substring(last_split));
        return temp_list;
    }

    private ArrayList<String> split_quotes(String command)
    {
        ArrayList<String> tokens = new ArrayList<>();                                       // know that whitespace \s is \\s in java and \| is \\| because we escape metacharacters

        int quote_start, quote_end = 0;
        for (int quote_scanning_index = 0; quote_scanning_index < command.length(); quote_scanning_index++)
        {
            char chr = command.charAt(quote_scanning_index);
            if(chr == '"' && is_quote_not_disabled(command, quote_scanning_index))
            {
                quote_start = quote_scanning_index;
                if ((quote_end = command.indexOf("\"", quote_start + 1)) != -1)
                {
                    while(!is_quote_not_disabled(command, quote_end))
                    {
                        quote_end = command.indexOf("\"", quote_end + 1);
                        if(quote_end == -1)
                        {
                            throw new RuntimeException("unmatched quote");
                        }
                    }
                    tokens.add(command.substring(quote_start + 1, quote_end));

                }
            }
            else if(chr == '\'' && is_quote_not_disabled(command, quote_scanning_index))
            {
                quote_start = quote_scanning_index;
                if ((quote_end = command.indexOf("\'", quote_start + 1)) != -1)
                {
                    while(!is_quote_not_disabled(command, quote_end))
                    {
                        quote_end = command.indexOf("\'", quote_end + 1);
                        if(quote_end == -1)
                        {
                            throw new RuntimeException("unmatched quote");
                        }
                    }
                    tokens.add(command.substring(quote_start + 1, quote_end));
                }
            }
            else if(chr == ' ' && is_quote_not_disabled(command, quote_scanning_index))
            {
                if(quote_end != -1 && quote_end < quote_scanning_index)
                {
                    tokens.add(command.substring(quote_end, quote_scanning_index));
                }
                quote_end = quote_scanning_index + 1;
            }
            else if(quote_scanning_index == command.length() - 1)
            {
                tokens.add(command.substring(quote_end));
            }
        }

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
}

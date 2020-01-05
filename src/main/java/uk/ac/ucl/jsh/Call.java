package uk.ac.ucl.jsh;

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
        command = commandSub(command); //execute command substitution
        ArrayList<String> tokens = splitToTokens(command);

        this.input = input;
        this.output = output;
        //DO IO REDIRECTION HERE
        ioRedirection(tokens);
        input = this.input;
        output = this.output;


        ArrayList<String> newTokens = new ArrayList<>();
        for (String c : tokens)
        {
            newTokens.addAll(glob(c));
        }
        tokens = newTokens;

        newTokens = new ArrayList<>();
        for (String c : tokens)
        {
            newTokens.add(stripQuotes(c));
        }
        tokens = newTokens;


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

    private void ioRedirection(ArrayList<String> tokens) throws FileNotFoundException
    {
        boolean in = false, out = false;
        for (int indexOfToken = 0; indexOfToken < tokens.size(); indexOfToken++)
        {
            String token = tokens.get(indexOfToken);
            String redirectionTarget;

            switch (token.charAt(0))
            {
                case '>':
                    if(out) throw new RuntimeException("[IO Redirection] Multiple output targets specified");
                    redirectionTarget = getRedirectionTarget(tokens, indexOfToken, token);
                    if(redirectionTarget.equals(""))
                    {
                        this.output = OutputStream.nullOutputStream();
                    }
                    else
                    {
                        this.output = new FileOutputStream(new File(currentDirectory + File.separator + redirectionTarget));
                    }
                    indexOfToken = 0;
                    out = true;
                    break;

                case '<':
                    if(in) throw new RuntimeException("[IO Redirection] Multiple input sources specified");
                    redirectionTarget = getRedirectionTarget(tokens, indexOfToken, token);
                    if(redirectionTarget.equals(""))
                    {
                        this.input = new ByteArrayInputStream(new byte[0]);
                    }
                    else
                    {
                        this.input = new FileInputStream(new File(currentDirectory + File.separator + redirectionTarget));
                    }
                    indexOfToken = 0;
                    in = true;
                    break;
            }
        }
    }

    private String getRedirectionTarget(ArrayList<String> tokens, int indexOfToken, String token)
    {
        String redirectionTarget;
        if (token.length() == 1)
        {
            try
            {
                redirectionTarget = tokens.get(indexOfToken + 1);
                if("<>".contains(Character.toString(redirectionTarget.charAt(0))))
                {
                    redirectionTarget = "";
                }
                else
                {
                    tokens.remove(indexOfToken + 1);
                }
                tokens.remove(indexOfToken);
            }
            catch (IndexOutOfBoundsException e)
            {
                redirectionTarget = "";
                tokens.remove(indexOfToken);
            }
        }
        else
        {
            redirectionTarget = token.substring(1);
            tokens.remove(token);
        }
        return redirectionTarget;
    }


    private boolean isQuoteNotDisabled(String command, int indexOfQuote)
    {
        return isQuoteNotDisabled(command, indexOfQuote, false);
    }

    private boolean isQuoteNotDisabled(String command, int indexOfQuote, boolean ignoreBackticks)
    {
        char chr = command.charAt(indexOfQuote);

        if (command.substring(0, indexOfQuote).lastIndexOf('`') != -1 &&
                command.substring(indexOfQuote + 1).indexOf('`') != -1 &&
                !ignoreBackticks)
        {
            return false;
        }

        switch (chr)
        {
            case '"':
            case '`':
                return isCharNotSurroundedBy(command, indexOfQuote, '\'');
            case '\'':
                return isCharNotSurroundedBy(command, indexOfQuote, '"');
            default:
                return isCharNotSurroundedBy(command, indexOfQuote, '"') &&
                        isCharNotSurroundedBy(command, indexOfQuote, '\'');
        }

    }


    private boolean isCharNotSurroundedBy(String command, int indexOfQuote, char quoteToCheck)
    {
        boolean insideQuote = false;
        int index;

        for (index = 0; index <= indexOfQuote; index++)
        {
            if (quoteToCheck == command.charAt(index)) //if char at index is one of ", ', or `
            {
                insideQuote = !insideQuote;
            }
        }
        return !insideQuote;
    }


    //this method was originally written to be called from somewhere else
    //which is why there are some checks that are irrelevant now
    private String commandSub(String command) throws IOException
    {
        int splitIndex, openingBackquoteIndex, closingBackquoteIndex;
        InputStream input = new ByteArrayInputStream(new byte[0]);
        String cmdoutput;
        for (splitIndex = 0; splitIndex < command.length(); splitIndex++) // iterates through the command line characters
            {
            char ch = command.charAt(splitIndex);                                               // isolates each character of the command line input
            if (ch == '`' && isQuoteNotDisabled(command, splitIndex))
            {
                //String command = cmdline.substring(prevDelimiterIndex, splitIndex).trim();
                openingBackquoteIndex = command.indexOf(ch);
                closingBackquoteIndex = command.indexOf(ch, splitIndex + 1);
                if (closingBackquoteIndex != -1) // check if quote is closed
                {
                    ByteArrayOutputStream subCommandOutput = new ByteArrayOutputStream();
                    String subCommand = command.substring(openingBackquoteIndex + 1, closingBackquoteIndex); // create a command of the
                    (new Sequence()).run(subCommand, input, subCommandOutput);
                    cmdoutput = new String(subCommandOutput.toByteArray());
                    cmdoutput = cmdoutput.replace("\n", " ").replace("\r", "").strip();


                    command = command.substring(0, openingBackquoteIndex) + "`" + cmdoutput + "`" + command.substring(closingBackquoteIndex + 1);
                    splitIndex = openingBackquoteIndex + cmdoutput.length();

                }
            }
        }
        return command;
    }

    private ArrayList<String> splitToTokens(String command)
    {
        ArrayList<String> tokens = new ArrayList<>();

        int lastQuoteEnd = 0;
        for (int scanningIndex = 0; scanningIndex < command.length(); scanningIndex++)
        {
            char chr = command.charAt(scanningIndex);
            if (chr == ' ' && isQuoteNotDisabled(command, scanningIndex, true))
            {
                tokens.add(command.substring(lastQuoteEnd, scanningIndex));
                lastQuoteEnd = scanningIndex + 1;
            }
            else if (scanningIndex == command.length() - 1)
            {
                tokens.add(command.substring(lastQuoteEnd));
            }

        }
        while(tokens.contains(""))
        {
            tokens.remove("");
        }
        return tokens;
    }

    private String stripQuotes(String command)
    {
        int index;
        StringBuilder stripped = new StringBuilder();
        char chr;
        for (index = 0; index < command.length(); index++)
        {
            chr = command.charAt(index);
            if ("\"'`".indexOf(chr) == -1 || !isQuoteNotDisabled(command, index))
            {
                stripped.append(chr);
            }
        }
        return stripped.toString();
    }

    private ArrayList<String> glob(String globString) throws IOException
    {
        boolean isAbsolute;
        ArrayList<String> globMatches = new ArrayList<>();
        File glob = new File(globString);
        if (!(isAbsolute = glob.isAbsolute()))
        {
            glob = new File(currentDirectory + File.separator + globString);
        }

        if (glob.isDirectory() && !isAbsolute)
        {
            globMatches.add(Paths.get(globString).toString().replace(currentDirectory, ""));
            return globMatches;
        }

        File parentFile;
        Path globWorkingDir = Paths.get(currentDirectory);
        if ((parentFile = glob.getParentFile()) != null && parentFile.isDirectory())
        {
            globWorkingDir = parentFile.toPath();
        }


        DirectoryStream<Path> stream;
        stream = Files.newDirectoryStream(globWorkingDir, glob.getName());
        String relPath = Paths.get(currentDirectory).relativize(globWorkingDir).toString();
        for (Path entry : stream)
        {
            if (relPath.equals("")) //empty path means both are equal
            {
                globMatches.add(entry.getFileName().toString());
            }
            else if (!isAbsolute)
            {
                globMatches.add(relPath + File.separator + entry.getFileName().toString());
            }
        }
        if (globMatches.isEmpty())
        {
            globMatches.add(globString);
        }
        return globMatches;
    }
}

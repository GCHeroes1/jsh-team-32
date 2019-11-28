package uk.ac.ucl.jsh;

import uk.ac.ucl.jsh.shellprorgams.SPFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jsh {

    // gets current directory
    protected static String currentDirectory = System.getProperty("user.dir");
    private static SPFactory spFactory = new SPFactory();

    // main interpretation function for the shell (what handles executing commands)
    public static void eval(String cmdline, OutputStream output) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(output);
        ArrayList<String> rawCommands = new ArrayList<>();                                      // assume will be used later for raw commands
		int closingPairIndex, prevDelimiterIndex = 0, splitIndex = 0;                           // used to be a comment censored by alex
		for (splitIndex = 0; splitIndex < cmdline.length(); splitIndex++) {                     // iterates through the command line characters  
			char ch = cmdline.charAt(splitIndex);                                               // isolates each character of the command line input  
			if (ch == ';')
			{
				String command = cmdline.substring(prevDelimiterIndex, splitIndex).trim();      // stores and trims the command line up to the semi colon as a command
				rawCommands.add(command);                                                       // adds that command to the arraylist of commands
				prevDelimiterIndex = splitIndex + 1;                                            // jumps to the section after semi-colon 
			}
			else if (ch == '\'' || ch == '\"')
			{                                                                                   // if it finds a quote (' or ")
				closingPairIndex = cmdline.indexOf(ch, splitIndex + 1);                         // finds index of second matching quote
				if (closingPairIndex == -1)                                                     // if there isnt one
				{
					continue;                                                                   
				}
				else
                {
					splitIndex = closingPairIndex;                                              // skips to after the closing quote (ignores enquoted areas)
				}
			}
        }
		if (!cmdline.isEmpty() && prevDelimiterIndex != splitIndex) {                           // if the command line wasnt empty and the line didnt end with a semi colon / can be tested very easily later
			String command = cmdline.substring(prevDelimiterIndex).trim();                      // creates a substring at the index and trims the word
			if (!command.isEmpty()) {
				rawCommands.add(command);                                                       // adds command to arraylist of commands if there wasnt a semi colon detected previosulsy
			}
		}
        for (String rawCommand : rawCommands) {                                                 // iterating through the arraylist of raw commands
            String spaceRegex = "[^\\s\"']+|\"([^\"]*)\"|'([^']*)'";                            // REGEX WARNING, misses "s" and "\" LAST PART SEEMS BUGGY NGL recognises anything following '
            ArrayList<String> tokens = new ArrayList<String>();                                  
            Pattern regex = Pattern.compile(spaceRegex);                                        // just compiles the regex 
            Matcher regexMatcher = regex.matcher(rawCommand);                                   // creates a "matcher" 
            String nonQuote;                                                                    
            while (regexMatcher.find()) {                                                       // as long as there is a match it will continue the while loop
                if (regexMatcher.group(1) != null || regexMatcher.group(2) != null) {           // checking if there is a first and second group (itd be null if it didnt exist?)
                    String quoted = regexMatcher.group(0).trim();                               // group(0) is the entire thing, trims it? 
                    tokens.add(quoted.substring(1,quoted.length()-1));                          // just removes the quotes 
                } else {                                                    
                    nonQuote = regexMatcher.group().trim();                                     // trims the entire regex 
                    ArrayList<String> globbingResult = new ArrayList<String>();                  
                    Path dir = Paths.get(currentDirectory);                                     // path object, represents operating system level directory 
                    DirectoryStream<Path> stream = Files.newDirectoryStream(dir, nonQuote);     // using the OS to do globbing for him 
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
            ArrayList<String> appArgs = new ArrayList<String>(tokens.subList(1, tokens.size()));// creates a variable holding all the arguments for the program invoked

            try
            {
                spFactory.getSP(appName).execute(appArgs.toArray(new String[0]));
            }
            catch (NullPointerException e)
            {
                throw new RuntimeException(appName + ": unknown application");
            }
        }
    }// this is a test comment because i'm frustrated <33333

    private void shell(String[] args)
    {
        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("jsh: wrong number of arguments");
                return;
            }
            if (!args[0].equals("-c")) {
                System.out.println("jsh: " + args[0] + ": unexpected argument");
            }
            try {
                eval(args[1], System.out);
            } catch (Exception e) {
                System.out.println("jsh: " + e.getMessage());
            }
        } else {
            System.out.println("Welcome to JSH!");
            Scanner input = new Scanner(System.in);
            try {
                while (true) {
                    String prompt = currentDirectory + "> ";
                    System.out.print(prompt);
                    try
                    {
                        String cmdline = input.nextLine();
                        eval(cmdline, System.out);
                    }
                    catch (Exception e)
                    {
                        System.out.println("jsh: " + e.getMessage());
                    }
                }
            }
            finally
            {
                input.close();
            }
        }
    }

    public static void main(String[] args)
    {
        Jsh jsh = new Jsh();
        jsh.shell(args);
    }

}

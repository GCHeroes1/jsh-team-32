package uk.ac.ucl.jsh;

import uk.ac.ucl.jsh.shellprorgams.SPFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
			{                                              // if it finds a quote (' or ")
				closingPairIndex = cmdline.indexOf(ch, splitIndex + 1);               // finds index of second matching quote
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

            //Here is the mess - does all the running the commands stuff
            switch (appName) {                                                                  // selects the app based on the first token at the command line
            case "cd":
                spFactory.getSP("cd").execute(appArgs.toArray(new String[0]));
                break;
            case "pwd":
                spFactory.getSP("pwd").execute(appArgs.toArray(new String[0]));
                break;
            case "ls":                                                                          // list directory
                File currDir;                                                                    
                if (appArgs.isEmpty()) {                                                         
                    currDir = new File(currentDirectory);                                       // if there is no argument to ls, it just puts the current directory as the directory to list 
                } else if (appArgs.size() == 1) {                                               
                    currDir = new File(appArgs.get(0));                                         // if there is an argument, list the directory specified by the argument
                } else {
                    throw new RuntimeException("ls: too many arguments");                       // otherwise throw an error 
                }
                try {
                    File[] listOfFiles = currDir.listFiles();                                   // carries the OS for files present in the directory 
                    boolean atLeastOnePrinted = false;                                          // avoids printing new line if no files are present
                    for (File file : listOfFiles) {                                             
                        if (!file.getName().startsWith(".")) {                                  // hides names that start with a .
                            writer.write(file.getName());                                       // prints it to terminal 
                            writer.write("\t");                                                 // line feed
                            writer.flush();                                                      
                            atLeastOnePrinted = true;                                            
                        }
                    }
                    if (atLeastOnePrinted) {                                                    // 
                        writer.write(System.getProperty("line.separator"));                     // prints a new line after its done printing 
                        writer.flush();
                    }
                } catch (NullPointerException e) {
                    throw new RuntimeException("ls: no such directory");                        // if it cant find the directory 
                }
                break;
            case "cat":                                                                          
                if (appArgs.isEmpty()) {                                                        // if there is no argument, cant cat nothing
                    throw new RuntimeException("cat: missing arguments");                        
                } else {                                                                        
                    for (String arg : appArgs) {                                                // for each file specified in the arguments 
                        Charset encoding = StandardCharsets.UTF_8;                              // print it using UTF 8 
                        File currFile = new File(currentDirectory + File.separator + arg);      // gets the absolute path of the file
                        if (currFile.exists()) {                                                // checks if it exists 
                            Path filePath = Paths.get(currentDirectory + File.separator + arg); // gets a path object from the filepath 
                            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) { // tries offering a buffered reader on the file 
                                String line = null;                                             // initialises the line variable 
                                while ((line = reader.readLine()) != null) {                    // for each line that isnt empty in the file 
                                    writer.write(String.valueOf(line));                         // print the contents 
                                    writer.write(System.getProperty("line.separator"));         // necessary 
                                    writer.flush();
                                }
                            } catch (IOException e) {                                           //
                                throw new RuntimeException("cat: cannot open " + arg);          //
                            }   
                        } else {    
                            throw new RuntimeException("cat: file does not exist");             //
                        }
                    }
                }
                break;
            case "echo":                                                                        // echo will print back what the shell interprets from the input string 
                spFactory.getSP("echo").execute(appArgs.toArray(new String[0]));
                break;
            case "head":
                spFactory.getSP("head").execute(appArgs.toArray(new String[0]));
                break;
            case "tail":
                spFactory.getSP("tail").execute(appArgs.toArray(new String[0]));
                break;
            case "grep":
                if (appArgs.size() < 2) {
                    throw new RuntimeException("grep: wrong number of arguments");
                }
                Pattern grepPattern = Pattern.compile(appArgs.get(0));
                int numOfFiles = appArgs.size() - 1;
                Path filePath;
                Path[] filePathArray = new Path[numOfFiles];
                Path currentDir = Paths.get(currentDirectory);
                for (int i = 0; i < numOfFiles; i++) {
                    filePath = currentDir.resolve(appArgs.get(i + 1));
                    if (Files.notExists(filePath) || Files.isDirectory(filePath) || 
                        !Files.exists(filePath) || !Files.isReadable(filePath)) {
                        throw new RuntimeException("grep: wrong file argument");
                    }
                    filePathArray[i] = filePath;
                }
                for (int j = 0; j < filePathArray.length; j++) {
                    Charset encoding = StandardCharsets.UTF_8;
                    try (BufferedReader reader = Files.newBufferedReader(filePathArray[j], encoding)) {
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            Matcher matcher = grepPattern.matcher(line);
                            if (matcher.find()) {
                                writer.write(line);
                                writer.write(System.getProperty("line.separator"));
                                writer.flush();
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("grep: cannot open " + appArgs.get(j + 1));
                    }
                }
                break;
            default:
                throw new RuntimeException(appName + ": unknown application");
            }
        }
    }

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
                    try {
                        String cmdline = input.nextLine();
                        eval(cmdline, System.out);
                    } catch (Exception e) {
                        System.out.println("jsh: " + e.getMessage());
                    }
                }
            } finally {
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

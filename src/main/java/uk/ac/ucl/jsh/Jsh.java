package uk.ac.ucl.jsh;

import uk.ac.ucl.jsh.shellprograms.SPFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Jsh {

    // gets current directory
    protected static String currentDirectory = System.getProperty("user.dir");
    static SPFactory spFactory = new SPFactory();

    // main interpretation function for the shell (what handles executing commands)
    private static void eval(String cmdline, OutputStream output) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(output);
        ArrayList<String> rawCommands = new ArrayList<>();                                      // assume will be used later for raw commands
		int closingPairIndex, prevDelimiterIndex = 0, splitIndex = 0;

		//this part splits into separate commands (seqs) if necessary;
        //ignores quotes altogether and sends it straight to the program
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
				closingPairIndex = cmdline.indexOf(ch, splitIndex + 1);               // finds index of second matching quote
				if (closingPairIndex != -1)                                                     // if there isn't one
				{
					splitIndex = closingPairIndex;                                              // skips to after the closing quote (ignores enquoted areas)
				}
            }

        }
		if (!cmdline.isEmpty() && prevDelimiterIndex != splitIndex) {                           // if the command line wasn't empty and the line didn't end with a semi colon
			String command = cmdline.substring(prevDelimiterIndex).trim();                      // creates a substring at the index and trims the word 
			if (!command.isEmpty()) {                                                           
				rawCommands.add(command);                                                       // adds command to arraylist of commands if there wasn't a semi colon detected previously 
			}
		}
        for (String rawCommand : rawCommands) {                                                 // iterating through the arraylist of raw commands
            new Pipe().run(rawCommand, output);
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
            try (Scanner input = new Scanner(System.in)) {
                while (true) {                                                                // this infinite loop just waits for the user to input and enter
                    String prompt = currentDirectory + "> ";
                    System.out.print(prompt);
                    try {
                        String cmdline = input.nextLine();
                        if(cmdline.equals("exit"))
                        {
                            break;
                        }
                        eval(cmdline, System.out);
                    } catch (Exception e) {
                        System.out.println("jsh: " + e.getMessage());
                    }
                }
            }
        }
    }

    public static void main(String[] args)
    {
        Jsh jsh = new Jsh();
        jsh.shell(args);
    }

}
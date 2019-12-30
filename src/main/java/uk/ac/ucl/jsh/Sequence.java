package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class Sequence extends Jsh implements CommandInterface{
    @Override
    public void run(String cmdline, InputStream input, OutputStream output) throws IOException {
        ArrayList<String> rawCommands = new ArrayList<>();                                      // assume will be used later for raw commands
		int closingPairIndex, prevDelimiterIndex = 0, splitIndex = 0;
        //Here is the code that separates by semicolons
        //this has just been copy/pasted in and will probz not work lolz
		//cmdline = cmdline.replace("\'", "\"");
		for (splitIndex = 0; splitIndex < cmdline.length(); splitIndex++) {                     // iterates through the command line characters  
			char ch = cmdline.charAt(splitIndex);                                               // isolates each character of the command line input  
			if (ch == ';')
			{
				if(splitIndex == 0)
				{
					throw new RuntimeException("jsh: unexpected token \';\'");
				}
				String command = cmdline.substring(prevDelimiterIndex, splitIndex).trim();      // stores and trims the command line up to the semi colon as a command 
				rawCommands.add(command);                                                       // adds that command to the arraylist of commands 
				prevDelimiterIndex = splitIndex + 1;                                            // jumps to the section after semi-colon 
			}
			else if (ch == '\'' || ch == '\"' || ch == '`')
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
            //System.out.println("blehehehe");                                                  // sooooo... this Sout somehow made our code work? all hail the "blehehehe" (love, Alex)
            new Pipe().run(rawCommand, input, output);
        }        
}

}

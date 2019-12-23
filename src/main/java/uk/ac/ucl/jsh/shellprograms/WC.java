

package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WC extends ShellProgram
{
    @Override
    public void execute(String[] args, ByteArrayInputStream stdin, ByteArrayOutputStream output) throws IOException
    {
        if (args.length < 1 || args.length > 2){
            throw new RuntimeException("wc: wrong arguments");
        }
        if (args.length == 1){
            if (args[0] == "-m"){
                //print the char count of stdin
            }
            else if(args[0] == "-w"){
                //print the word (space) count of stdin
            }
            else if(args[0] == "-l"){
                //print the line count of stdin
            }
            else{
                throw new RuntimeException("wc: wrong arguments");
            }
        } 
        else{
            if (args[0] == "-m"){
                //print the char count of args[1]
            }
            else if(args[0] == "-w"){
                //print the word (space) count of args[1]
            }
            else if(args[0] == "-l"){
                //print the line count of args[1]
            }
            else{
                throw new RuntimeException("wc: wrong arguments");
            }
        }
    }
}
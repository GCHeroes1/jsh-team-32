package uk.ac.ucl.jsh;

import uk.ac.ucl.jsh.shellprograms.SPFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class Jsh {

    // gets current directory
    protected static String currentDirectory = System.getProperty("user.dir");
    static SPFactory spFactory = new SPFactory();

    public Jsh(String pwd){currentDirectory = pwd;}

    public Jsh()
    {
    }

    // main interpretation function for the shell (what handles executing commands)
    void eval(String cmdline, OutputStream output) throws IOException {
        InputStream empty_input_stream = new ByteArrayInputStream(new byte[0]);
        Sequence sequence = new Sequence();
        sequence.run(cmdline, empty_input_stream, output);
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
                System.err.println("jsh: " + e.getMessage());
                //System.out.println("jsh: " + e.getMessage());
            }
        } else {
            //System.out.println("Welcome to JSH!");
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
                        System.err.println("jsh: " + e.getMessage());
                        //System.out.println("jsh: " + e.getMessage());
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
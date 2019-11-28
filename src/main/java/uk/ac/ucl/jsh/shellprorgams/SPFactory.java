package uk.ac.ucl.jsh.shellprorgams;

import java.util.HashMap;

public class SPFactory
{
    private HashMap<String, ShellProgram> programs = new HashMap<>();
    public SPFactory()
    {
        programs.put("cat", new Cat());
        programs.put("cd", new Cd());
        programs.put("echo", new Echo());
        programs.put("grep", new Grep());
        programs.put("head", new Head());
        programs.put("ls", new Ls());
        programs.put("pwd", new Pwd());
        programs.put("tail", new Tail());
    }

    public ShellProgram getSP(String program)
    {
        return programs.get(program);
    }
}

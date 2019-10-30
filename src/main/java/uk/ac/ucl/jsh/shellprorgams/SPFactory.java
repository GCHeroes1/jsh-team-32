package uk.ac.ucl.jsh.shellprorgams;

import java.util.HashMap;

public class SPFactory
{
    private HashMap<String, ShellProgram> programs = new HashMap<>();
    public SPFactory()
    {
        programs.put("grep", new Grep());
        programs.put("pwd", new Pwd());
        programs.put("cat", new Cat());
        programs.put("cd", new Cd());
    }

    public ShellProgram getSP(String program)
    {
        return programs.get(program);
    }
}

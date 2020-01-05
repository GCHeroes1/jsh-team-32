package uk.ac.ucl.jsh.shellprograms;

import java.util.HashMap;

public class SPFactory
{
    private HashMap<String, ShellProgram> programs = new HashMap<>();

    public SPFactory()
    {
        programs.put("cat", new Cat());
        programs.put("cd", new Cd());
        programs.put("echo", new Echo());
        programs.put("find", new Find());
        programs.put("grep", new Grep());
        programs.put("head", new Head());
        programs.put("ls", new Ls());
        programs.put("pwd", new Pwd());
        programs.put("sed", new Sed());
        programs.put("tail", new Tail());
        programs.put("wc", new Wc());
    }

    public ShellProgram getSP(String program)
    {
        ShellProgram instance = programs.get(program);
        if(instance == null)
        {
            throw new RuntimeException("appName: program not found");
        }

        return instance;
    }
}

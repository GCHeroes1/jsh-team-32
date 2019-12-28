package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

public class Find  extends ShellProgram {

    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);

        String pattern;
        File working_dir = new File(currentDirectory);
        File find_dir = new File(currentDirectory);
        switch (args.length)
        {
            case 2:
                if(args[0].equals("-name"))
                {
                    pattern = args[1];
                }
                else
                {
                    throw new RuntimeException("find: wrong arguments - expected \"-name\"");
                }
                break;
            case 3:
                find_dir = new File(args[0]);
                if(!find_dir.isAbsolute())
                {
                    find_dir = new File(currentDirectory, args[0]);
                }
                if(args[1].equals("-name"))
                {
                    pattern = args[2];
                }
                else
                {
                    throw new RuntimeException("find: wrong arguments - expected \"-name\"");
                }
                break;
            default:
                throw new RuntimeException("find: wrong number of arguments");
        }


        Path target_path = Paths.get(find_dir.getCanonicalPath());
        Path working_path = Paths.get(working_dir.getCanonicalPath());
        Stream<Path> stream = Files.walk(target_path);
        PathMatcher pm = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        stream.filter(path1 -> pm.matches(path1.getFileName())).forEach(found_file_path -> this.relativize_path(working_path, target_path, found_file_path, str_to_bytes));
    }

    private void relativize_path(Path path_source, Path target_path, Path found_file_path, OutputStreamWriter osw)
    {
        try
        {
            if(path_source.equals(target_path))
            {
                osw.write("." + File.separator);
            }
            osw.write(path_source.relativize(found_file_path).toString());
            osw.write(System.getProperty("line.separator"));
            osw.flush();
        }
        catch (IOException e)
        {
            throw new RuntimeException("find: IOException\n" + e);
        }
    }
}

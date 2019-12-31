package uk.ac.ucl.jsh.shellprograms;

import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

public class Find extends ShellProgram
{

    @Override
    public void execute(String[] args, InputStream stdin, OutputStream stdout) throws IOException
    {
        OutputStreamWriter str_to_bytes = new OutputStreamWriter(stdout);

        boolean is_absolute = false;
        boolean has_path = false;
        String pattern;
        File working_dir = new File(currentDirectory);
        File find_dir = new File(currentDirectory);
        switch (args.length)
        {
            case 2:
                if (args[0].equals("-name"))
                {
                    pattern = args[1];
                }
                else
                {
                    throw new RuntimeException("find: wrong arguments - expected \"-name\"");
                }
                break;
            case 3:
                has_path = true;
                is_absolute = Paths.get(args[0]).isAbsolute();
                if(is_absolute)
                {
                    find_dir = Paths.get(args[0]).toFile();
                }
                else
                {
                    find_dir = Paths.get(currentDirectory + File.separator + args[0]).toFile();
                }
                if (args[1].equals("-name"))
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
        Stream<Path> stream = Files.walk(target_path);
        PathMatcher pm = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        for(Path found_file_path : (Iterable<Path>) stream.filter(path1 -> pm.matches(path1.getFileName()))::iterator)
        {
            if(is_absolute)
            {
                write_line_to_output(str_to_bytes, found_file_path.toString());
            }
            else if(has_path)
            {
                write_line_to_output(str_to_bytes, args[0] + File.separator + target_path.relativize(found_file_path).toString());
            }
            else
            {
                write_line_to_output(str_to_bytes, "." + File.separator + target_path.relativize(found_file_path).toString());

            }
        }
        stream.close();
    }

}

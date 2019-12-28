package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class FindTest {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private String file_sep = File.separator;


    public FindTest() {
        //jsh = new Jsh(System.getProperty("user.dir"));
        out.reset();
    }

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setup_file_env() throws IOException {
        workingDir = temporaryFolder.newFolder("testfolder");
        //System.out.println(workingDir.getCanonicalPath());
        FileUtils.copyDirectory(new File("src/test/test_template"), workingDir);

        jsh = new Jsh(workingDir.getCanonicalPath());

    }



    @Test
    public void test_find()  {
        try {
            jsh.eval("find -name file.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"." + file_sep + "dir2"
                + file_sep + "subdir" + file_sep + "file.txt"}, output.split("\r\n|\n|\t"));
    }

    @Test
    public void test_find_pattern()  {
        try {
            jsh.eval("find -name '*.txt'", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String outputstr = new String(out.toByteArray());
        outputstr = outputstr.strip();

        String[] expected = new String[]{
                "." + file_sep + "dir2" + file_sep + "subdir" + file_sep + "file.txt",
                "." + file_sep + "test.txt",
                "." + file_sep + "dir1" + file_sep + "file1.txt",
                "." + file_sep + "dir1" + file_sep + "file2.txt",
                "." + file_sep + "dir1" + file_sep + "longfile.txt"};
        Arrays.sort(expected);

        String[] output = outputstr.split("\r\n|\n|\t");
        Arrays.sort(output);

        assertArrayEquals(expected, output);
    }

    @Test
    public void test_find_dir()
    {
        try {
            jsh.eval("find dir1 -name '*.txt'", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{
                "dir1" + file_sep + "file1.txt",
                "dir1" + file_sep + "file2.txt",
                "dir1" + file_sep + "longfile.txt"}, output.split("\r\n|\n|\t"));
    }
}

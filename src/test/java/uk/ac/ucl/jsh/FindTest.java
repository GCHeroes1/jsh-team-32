package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import uk.ac.ucl.jsh.shellprograms.Find;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class FindTest {
    private Jsh jsh;
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
        File workingDir = temporaryFolder.newFolder("testfolder");
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


    //============================================


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test_find_no_name() throws IOException
    {
        thrown.expect(RuntimeException.class);
        jsh.eval("find -n '*.txt'", out);
    }

    @Test
    public void test_find_no_name_with_dir() throws IOException
    {
        thrown.expect(RuntimeException.class);
        jsh.eval("find dir1 -n '*.txt'", out);
    }

    @Test
    public void test_find_too_many_args() throws IOException
    {
        thrown.expect(RuntimeException.class);
        jsh.eval("find dir1 -n '*.txt' '*.pub", out);
    }

    @Test
    public void test_find_too_few_args() throws IOException
    {
        thrown.expect(RuntimeException.class);
        jsh.eval("find dir1", out);
    }

    @Test
    public void test_find_bad_out() throws IOException
    {
        thrown.expect(RuntimeException.class);
        //jsh.eval("find dir1", out);
        InputStream is = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Expected IOException");
            }
        };

        OutputStream os = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("expected IOException");
            }
        };
        (new Find()).execute(new String[]{"-name", "*.txt"}, is, os);
    }
}

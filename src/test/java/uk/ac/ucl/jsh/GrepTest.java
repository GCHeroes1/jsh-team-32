package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class GrepTest {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public GrepTest() {
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
    public void test_grep()  {
        try {
            jsh.eval("grep AAA dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"AAA", "AAA"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_grep_no_match()  {
        try {
            jsh.eval("grep DDD dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("", output);
    }

    @Test
    public void test_grep_stdin_no_match()  {
        try {
            jsh.eval("echo AAA | grep DDD", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("", output);
    }

    @Test
    public void test_grep_re()  {
        try {
            jsh.eval("grep 'A..' dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"AAA", "AAA"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_grep_files()  {
        try {
            jsh.eval("grep '...' dir1/file1.txt dir1/file2.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"AAA", "BBB",
                "AAA", "CCC"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_grep_stdin()  {
        try {
            jsh.eval("cat dir1/file1.txt dir1/file2.txt | grep '...'", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"AAA", "BBB",
                "AAA", "CCC"}, output.split("\r\n|\n"));
    }


    //=========================================


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test_grep_bad_file() throws IOException {
        thrown.expect(IOException.class);
        jsh.eval("grep '...' badfile.txt", out);
    }
}

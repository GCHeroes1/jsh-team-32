package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.*;
//import java.util.ArrayList;
//import java.util.Arrays;

import static org.junit.Assert.*;

public class PwdCdTest {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public PwdCdTest() {
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
    public void test_pwd() throws IOException {
        try {
            jsh.eval("pwd", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals(workingDir.getCanonicalPath(), output);
    }

    @Test
    public void test_cd_pwd() throws IOException {
        try {
            jsh.eval("cd dir1; pwd", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals(workingDir.getCanonicalPath() + File.separator + "dir1", output);
    }


    //==================================


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test_cd_no_args() throws IOException
    {
        thrown.expect(RuntimeException.class);
        jsh.eval("cd", out);
    }

    @Test
    public void test_cd_too_many_args() throws IOException
    {
        thrown.expect(RuntimeException.class);
        jsh.eval("cd dir1 dir2", out);
    }

    @Test
    public void test_cd_nonexistent_dir() throws IOException
    {
        thrown.expect(RuntimeException.class);
        jsh.eval("cd dir3", out);
    }

    @Test
    public void test_cd_file() throws IOException
    {
        thrown.expect(RuntimeException.class);
        jsh.eval("cd test.txt", out);
    }

    @Test
    public void test_cd_abs_dir() throws IOException
    {
        String os = System.getProperty("os.name");
        if(os.contains("Linux"))
        {
            jsh.eval("cd /", out);
            out.reset();

            jsh.eval("pwd", out);
            String output = new String(out.toByteArray());
            output = output.strip();
            assertEquals("/", output);
        }
        else if(os.contains("Windows"))
        {
            jsh.eval("cd C:/", out);
            out.reset();

            jsh.eval("pwd", out);
            String output = new String(out.toByteArray());
            output = output.strip();
            assertEquals("C:\\", output);
        }
    }
}

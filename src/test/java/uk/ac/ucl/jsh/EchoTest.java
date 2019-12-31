package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class EchoTest
{
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public EchoTest() {
        //jsh = new Jsh(System.getProperty("user.dir"));
        out.reset();
    }

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setup_file_env() throws IOException
    {
        workingDir = temporaryFolder.newFolder("testfolder");
        FileUtils.copyDirectory(new File("src/test/test_template"), workingDir);

        jsh = new Jsh(workingDir.getCanonicalPath());
    }


    @Test
    public void test_echo() throws IOException {
        jsh.eval("echo hello world", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("hello world", output);
    }

    @Test
    public void test_echo_no_args() throws IOException {
        jsh.eval("echo", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("", output);
    }
}

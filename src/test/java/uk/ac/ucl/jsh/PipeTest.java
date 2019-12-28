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

import static org.junit.Assert.assertEquals;

public class PipeTest {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public PipeTest() {
        //jsh = new Jsh(System.getProperty("user.dir"));
        out.reset();
    }

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup_file_env() throws IOException {
        workingDir = temporaryFolder.newFolder("testfolder");
        //System.out.println(workingDir.getCanonicalPath());
        FileUtils.copyDirectory(new File("src/test/test_template"), workingDir);

        jsh = new Jsh(workingDir.getCanonicalPath());
    }

    @Test
    public void test_pipe() throws IOException {
        jsh.eval("echo AAA | sed 's/A/B/'", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("BAA", output);
    }

    @Test
    public void test_pipe_chain() throws IOException {
        jsh.eval("echo AAA | sed 's/A/C/' | sed 's/A/B/'", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("CBA", output);
    }

    @Test
    public void test_pipe_exception() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("ls dir3 | echo foo", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("", output);
    }


}

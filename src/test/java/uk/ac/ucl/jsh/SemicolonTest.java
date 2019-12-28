package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class SemicolonTest {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public SemicolonTest() {
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
    public void test_semicolon() {
        try {
            jsh.eval("echo AAA; echo BBB", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertArrayEquals(new String[]{"AAA", "BBB"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_semicolon_chain() {
        try {
            jsh.eval("echo AAA; echo BBB; echo CCC", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertArrayEquals(new String[]{"AAA", "BBB", "CCC"}, output.split("\r\n|\n"));
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void test_semicolon_exception() throws IOException{
        thrown.expect(RuntimeException.class);
        jsh.eval("ls dir3; echo BBB", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("", output);
    }



}

package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class IORedirectionTest {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public IORedirectionTest() {
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
    public void test_input_redirection()  {
        try {
            jsh.eval("cat < dir1/file2.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("CCC", output);
    }

    @Test
    public void test_input_redirection_in_front()  {
        try {
            jsh.eval("< dir1/file2.txt cat", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("CCC", output);
    }

    @Test
    public void test_input_redirection_no_space()  {
        try {
            jsh.eval("cat <dir1/file2.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("CCC", output);
    }

    @Test
    public void test_output_redirection() throws IOException {
        try {
            jsh.eval("echo foo > anewfile.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }

        StringBuilder filecontent = new StringBuilder();
        try {
            File outputfile = new File(workingDir + File.separator + "anewfile.txt");
            BufferedReader bfr = new BufferedReader(new FileReader(outputfile));
            String line;
            while ((line = bfr.readLine()) != null) {
                filecontent.append(line);
            }
        } catch (FileNotFoundException e) {
            fail("File not created");
        }
        assertEquals("foo", filecontent.toString());
    }

    @Test
    public void test_output_redirection_overwrite() throws IOException {
        try {
            jsh.eval("echo foo > test.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }

        StringBuilder filecontent = new StringBuilder();
        try
        {
            File outputfile = new File(workingDir + File.separator + "test.txt");
            BufferedReader bfr = new BufferedReader(new FileReader(outputfile));
            String line;
            while ((line = bfr.readLine()) != null) {
                filecontent.append(line);
            }
        }
        catch (FileNotFoundException e)
        {
            fail("File not created");
        }
        assertEquals("foo", filecontent.toString());
    }


    //===================================================

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test_io_redirect_no_target() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("cat test.txt >", out);
        //String output = new String(out.toByteArray());
        //output = output.strip();
        //assertEquals("CCC", output);
    }

    @Test
    public void test_output_redirection_no_space() throws IOException {
        jsh.eval("echo hello >temp.txt", out);
        String output = new String(out.toByteArray());

        StringBuilder filecontent = new StringBuilder();
        try {
            File outputfile = new File(workingDir + File.separator + "temp.txt");
            BufferedReader bfr = new BufferedReader(new FileReader(outputfile));
            String line;
            while ((line = bfr.readLine()) != null) {
                filecontent.append(line);
            }
        } catch (FileNotFoundException e) {
            fail("File not created");
        }
        assertEquals("hello", filecontent.toString());
    }

    @Test
    public void test_io_redirect_invalid_read() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("echo abc < lol.txt", out);
        //String output = new String(out.toByteArray());
        //output = output.strip();
        //assertEquals("CCC", output);
    }

    @Test
    public void test_io_redirect_invalid_write() throws IOException //doesnt work properly 
    {
        thrown.expect(IOException.class);
        File tf = temporaryFolder.newFile("lol.txt");
        tf.setWritable(false, true);
        jsh.eval("echo abc > lol.txt", out);
    }
}

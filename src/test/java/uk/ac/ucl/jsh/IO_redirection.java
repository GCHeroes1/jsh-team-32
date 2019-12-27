package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class IO_redirection {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public IO_redirection() {
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
            jsh.eval("echo foo > newfile.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }

        StringBuilder filecontent = new StringBuilder();
        try {
            File outputfile = new File("newfile.txt");
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
            File outputfile = new File("test.txt");
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



}
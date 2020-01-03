package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.*;
// import java.nio.charset.Charset;
// import java.nio.charset.StandardCharsets;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;

import static org.junit.Assert.*;
//import static uk.ac.ucl.jsh.Jsh.currentDirectory;

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
            bfr.close();
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
            bfr.close();
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
    }

    @Test
    public void test_output_redirection_no_space() throws IOException {
        jsh.eval("echo hello >output.txt", out);

        StringBuilder filecontent = new StringBuilder();
        try {
            File outputfile = new File(workingDir + File.separator + "output.txt");
            BufferedReader bfr = new BufferedReader(new FileReader(outputfile));
            String line;
            while ((line = bfr.readLine()) != null) {
                filecontent.append(line);
            }
            bfr.close();
        } catch (FileNotFoundException e) {
            fail("File not created");
        }
        assertEquals("hello", filecontent.toString());
    }

    @Test
    public void test_io_redirect_invalid_read() throws IOException {
        thrown.expect(IOException.class);
        jsh.eval("echo abc < bad.txt", out);
    }

    @Test
    public void test_io_redirect_out_too_many_files() throws IOException
    {
        thrown.expect(RuntimeException.class);
        jsh.eval("echo abc > a.txt > b.txt", out);
    }

    @Test
    public void test_io_redirect_in_too_many_files() throws IOException
    {
        thrown.expect(RuntimeException.class);
        jsh.eval("echo abc < dir1/file1.txt < dir1/file2.txt", out);
    }

}

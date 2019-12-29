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

public class LsTest {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public LsTest() {
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
    public void test_ls() {
        try {
            jsh.eval("ls", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String outputstr = new String(out.toByteArray());
        outputstr = outputstr.strip();

        String[] expected = new String[]{
                "dir1",
                "dir2",
                "test.txt"
        };
        Arrays.sort(expected);

        String[] output = outputstr.split("\r\n|\n|\t");
        Arrays.sort(output);

        assertArrayEquals(expected, output);
    }

    @Test
    public void test_ls_dir() {
        try {
            jsh.eval("ls dir1", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String outputstr = new String(out.toByteArray());
        outputstr = outputstr.strip();

        String[] expected = new String[]{
                "file1.txt",
                "file2.txt",
                "longfile.txt"
        };
        Arrays.sort(expected);

        String[] output = outputstr.split("\r\n|\n|\t");
        Arrays.sort(output);


        assertArrayEquals(expected, output);
    }

    @Test
    public void test_ls_hidden() {
        try {
            jsh.eval("ls dir2/subdir", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"file.txt"}, output.split("[\n\t]"));
    }

    @Test
    public void test_unsafe_ls() throws IOException {
        jsh.eval("_ls dir3; echo AAA > newfile.txt", out);

        StringBuilder filecontent = new StringBuilder();
        try
        {
            File outputfile = new File("newfile.txt");
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

        assertEquals("AAA", filecontent.toString());
    }


    //========================================================


    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void test_unsafe_ls_no_exception() throws IOException {
        jsh.eval("_ls dir1; echo AAA > newfile.txt", out);

        StringBuilder filecontent = new StringBuilder();
        try
        {
            File outputfile = new File("newfile.txt");
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

        assertEquals("AAA", filecontent.toString());
    }

    @Test
    public void test_ls_too_many_args() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("ls dir1 dir2", out);
    }


}

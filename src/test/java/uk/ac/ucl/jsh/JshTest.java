package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.*;

public class JshTest {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public JshTest() {
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
    public void test_echo() throws IOException {
        jsh.eval("echo hello world", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("hello world", output);
    }




    @Test
    public void test_globbing()  {
        try {
            jsh.eval("echo *", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String outputstr = new String(out.toByteArray());
        outputstr = outputstr.strip();

        String[] expected = new String[]{"dir1", "dir2", "test.txt"};
        Arrays.sort(expected);

        String[] output = outputstr.split("\\s");
        Arrays.sort(output);

        assertArrayEquals(expected, output);
    }

    @Test
    public void test_globbing_dir()  {
        try
        {
//            jsh.eval("echo dir1/*.txt", out);
            jsh.eval("echo dir1" + File.separator + "*.txt", out);
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        String outputstr = new String(out.toByteArray());
        outputstr = outputstr.strip();

        String[] expected = new String[]{
                "dir1" + File.separator + "file1.txt",
                "dir1" + File.separator + "file2.txt",
                "dir1" + File.separator + "longfile.txt"};
        Arrays.sort(expected);

        String[] output = outputstr.split("\\s");
        Arrays.sort(output);

        assertArrayEquals(expected, output);
    }


    //======================================


    @Rule
    public ExpectedException thrown = ExpectedException.none();

//    @Before
//    public void setup_file_en() throws IOException {
//        workingDir = temporaryFolder.newFolder("testfolder");
//        //System.out.println(workingDir.getCanonicalPath());
//        FileUtils.copyDirectory(new File("src/test/test_template"), workingDir);
//
//        jsh = new Jsh();
//    }

    @Test
    public void test_unknown_app() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("hi there", out);
        String output = new String(out.toByteArray());
        //output = output.strip();
        //Scanner scn = new Scanner(in);
        //assertEquals("hello world", output);
    }

    @Test
    public void test_shell() throws IOException {
        String[] args = new String[]{"ls", "badDir"};
        final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
        PrintStream p = new PrintStream(myOut, true, StandardCharsets.UTF_8);
        System.setErr(p);
        Jsh.main(args);
        String data = new String(myOut.toByteArray(), StandardCharsets.UTF_8);
        assertEquals("jsh: badDir: unknown application\n", data);
        //output = output.strip();
        //Scanner scn = new Scanner(in);
        //assertEquals("hello world", output);
    }

}

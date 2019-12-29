package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.*;

import static org.junit.Assert.*;

public class TailTest {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public TailTest() {
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
    public void test_tail() {
        try {
            jsh.eval("tail dir1/longfile.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"11", "12", "13", "14", "15", "16",
                "17", "18", "19", "20"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_tail_stdin() {
        try {
            jsh.eval("tail < dir1/longfile.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"11", "12", "13", "14", "15", "16",
                "17", "18", "19", "20"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_tail_n5()  {
        try {
            jsh.eval("tail -n 5 dir1/longfile.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"16", "17",
                "18", "19", "20"}, output.split("\r\n|\n"));
    }


    @Test
    public void test_tail_n50()  {
        try {
            jsh.eval("tail -n 50 dir1/longfile.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"1", "2", "3", "4", "5",
                "6", "7", "8", "9", "10", "11", "12", "13", "14",
                "15", "16", "17", "18", "19", "20"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_tail_n0()  {
        try {
            jsh.eval("tail -n 0 dir1/longfile.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("", output);
    }


    //======================================================


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test_tail_wrong_arguments() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("tail -n 0 dir1/longfile.txt anotherargument", out);
//        String output = new String(out.toByteArray());
//        output = output.strip();
//        assertEquals("", output);
    }

    @Test
    public void test_tail_not_n() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("tail -q 0 dir1/longfile.txt", out);
//        String output = new String(out.toByteArray());
//        output = output.strip();
//        assertEquals("", output);
    }

    @Test
    public void test_tail_not_number_file() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("tail -n a dir1/longfile.txt", out);
//        String output = new String(out.toByteArray());
//        output = output.strip();
//        assertEquals("", output);
    }

    @Test
    public void test_tail_not_number_stdin() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("tail -n a < dir1/longfile.txt", out);
//        String output = new String(out.toByteArray());
//        output = output.strip();
//        assertEquals("", output);
    }

    @Test
    public void test_tail_bad_file() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("tail -n 5 dir1/file5.txt", out);
//        String output = new String(out.toByteArray());
//        output = output.strip();
//        assertEquals("", output);
    }

    @Test
    public void test_tail_stdin_n5() {
        try {
            jsh.eval("tail -n 5 < dir1/longfile.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"16", "17", "18", "19", "20"}, output.split("\r\n|\n"));
    }
}

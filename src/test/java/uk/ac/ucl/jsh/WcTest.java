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
//import java.util.concurrent.ExecutorService;

import static org.junit.Assert.*;

public class WcTest {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public WcTest() {
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
    public void test_wc()  {
        try {
            jsh.eval("wc dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"3", "3", "12"}, output.split("\\s"));
    }

    @Test
    public void test_wc_stdin()  {
        try {
            jsh.eval("wc < dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"3", "3", "12"}, output.split("\\s"));
    }

    @Test
    public void test_wc_m()  {
        try {
            jsh.eval("wc -m < dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("12", output);
    }

    @Test
    public void test_wc_w()  {
        try {
            jsh.eval("wc -w < dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("3", output);
    }

    @Test
    public void test_wc_l()  {
        try {
            jsh.eval("wc -l < dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("3", output);
    }

    @Test
    public void test_wc_files()  {
        try {
            jsh.eval("wc -l  dir1/file1.txt dir1/file2.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("4", output);
    }


    //==============================================


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test_wc_bad_file() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("wc nofile.txt", out);
    }

    @Test
    public void test_wc_spaces_in_file()  {
        try {
            jsh.eval("wc dir1/file3.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"5", "3", "15"}, output.split("\\s"));
    }

    @Test
    public void test_wc_bad_arg() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("wc -x nofile.txt", out);
    }

    @Test
    public void test_wc_bad_dir() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("wc -w dir3", out);
    }

//    @Test
//    public void test_wc_bad_dir() throws IOException {
//        thrown.expect(RuntimeException.class);
//        jsh.eval("wc -w dir3", out);
//    }
}

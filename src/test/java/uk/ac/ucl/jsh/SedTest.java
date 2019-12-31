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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class SedTest {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public SedTest() {
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
    public void test_sed()  {
        try {
            jsh.eval("sed 's/A/D/' dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"DAA", "BBB", "DAA"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_sed_stdin()  {
        try {
            jsh.eval("sed 's/A/D/' < dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"DAA", "BBB", "DAA"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_sed_separator()  {
        try {
            jsh.eval("sed 's|A|D|' dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"DAA", "BBB", "DAA"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_sed_g()  {
        try {
            jsh.eval("sed 's/A/D/g' dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"DDD", "BBB", "DDD"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_sed_re()  {
        try {
            jsh.eval("sed 's/../DD/g' dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"DDA", "DDB", "DDA"}, output.split("\r\n|\n"));
    }


    //============================================


    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void test_bad_pattern_no_s() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("sed '/./B/g' dir1/file1.txt", out);
    }

    @Test
    public void test_bad_pattern_too_many() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("sed 's/./B/q/g' dir1/file1.txt", out);
    }

    @Test
    public void test_bad_pattern_not_g() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("sed 's/./B/d' dir1/file1.txt", out);
    }


    @Test
    public void test_bad_file() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("sed 's/./B/g' dir1/file5.txt", out);
    }


    @Test
    public void test_sed_too_many_args() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval("sed 's/./B/g' dir1/file1.txt dir1/file2.txt", out);
    }

    @Test
    public void test_sed_unreadable_file() throws IOException {
        File badfile = temporaryFolder.newFile("bad.txt");
        badfile.setReadable(false);
        thrown.expect(RuntimeException.class);
        jsh.eval("sed 's/./B/g' bad.txt", out);
    }

}

package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class Find {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public Find() {
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
    public void test_find()  {
        try {
            jsh.eval("find -name file.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"./dir2/subdir/file.txt"}, output.split("\r\n|\n|\t"));
    }

    @Test
    public void test_find_pattern()  {
        try {
            jsh.eval("find -name '*.txt'", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"./dir2/subdir/file.txt",
                "./test.txt",
                "./dir1/file1.txt",
                "./dir1/file2.txt",
                "./dir1/longfile.txt"}, output.split("\r\n|\n|\t"));
    }

    @Test
    public void test_find_dir()
    {
        try {
            jsh.eval("find dir1 -name '*.txt'", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"./dir1/file1.txt",
                "./dir1/file2.txt",
                "./dir1/longfile.txt"}, output.split("\r\n|\n|\t"));
    }
}

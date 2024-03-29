package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.*;
// import java.nio.file.NoSuchFileException;
// import java.util.ArrayList;
// import java.util.Arrays;

import static org.junit.Assert.*;

public class CatTest {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public CatTest() {
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
    public void test_cat() {
        try {
            jsh.eval("cat dir1/file1.txt dir1/file2.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"AAA", "BBB", "AAA", "CCC"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_cat_stdin() throws IOException
    {
        jsh.eval("cat < dir1/file1.txt", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"AAA", "BBB", "AAA"}, output.split("\r\n|\n"));
    }


    //=====================================

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test_cat_file_not_exist() throws IOException
    {
        thrown.expect(IOException.class);
        jsh.eval("cat badfile.txt", out);
    }
}

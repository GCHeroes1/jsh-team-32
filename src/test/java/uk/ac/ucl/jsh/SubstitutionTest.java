package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SubstitutionTest
{
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public SubstitutionTest() {
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
    public void test_substitution() throws IOException {
        jsh.eval("echo `echo foo`", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("foo", output);
    }

    @Test
    public void test_substitution_inside_arg() throws IOException {
        jsh.eval("echo a`echo a`a", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("aaa", output);
    }

    @Test
    public void test_substitution_splitting() throws IOException {
        jsh.eval("echo `echo foo  bar`", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("foo bar", output);
    }

    @Test
    public void test_substitution_wc_find() throws IOException {
        jsh.eval("wc -l `find -name '*.txt'`", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("26", output);
    }

    @Test
    public void test_substitution_semicolon() throws IOException {
        jsh.eval("echo `echo foo; echo bar`", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("foo bar", output);
    }

    @Test
    public void test_substitution_keywords() throws IOException {
        jsh.eval("echo `cat test.txt`", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("''", output);
    }

    @Test
    public void test_substitution_app() throws IOException {
        jsh.eval("`echo echo` foo", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("foo", output);
    }



}

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
import static org.junit.Assert.fail;

public class QuotingTest {
    private Jsh jsh;
    private File workingDir;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public QuotingTest() {
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
    public void test_single_quote() {
        try {
            jsh.eval("echo 'a  b'", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("a  b", output);
    }
    @Test
    public void test_quote_keyword() {
        try {
            jsh.eval("echo ';'", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals(";", output);
    }

    @Test
    public void test_double_quotes() {
        try {
            jsh.eval("echo \"a  b\"", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("a  b", output);
    }
    @Test
    public void test_substitution_double_quotes() {
        try {
            jsh.eval("echo \"`echo foo`\"", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("foo", output);
    }

    @Test
    public void test_double_quotes_nested() {
        try {
            jsh.eval("echo \"a `echo \"b\"`\"", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("a b", output);
    }

    @Test
    public void test_disabled_double_quotes() {
        try {
            jsh.eval("echo '\"\"'", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("\"\"", output);
    }

    @Test
    public void test_quote_splitting() {
        try {
            jsh.eval("echo a\"b\"c", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("abc", output);
    }


    //============================================


    @Test
    public void test_pipe_in_cmd_sub() throws IOException {
        jsh.eval("echo `echo abc | sed 's/a/b/g'`", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("bbc", output);
    }

    @Test
    public void test_redirection_in_quote() throws IOException
    {
        jsh.eval("echo abc \">\" def", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("abc > def", output);
    }

    @Test
    public void test_pipe_character_in_quote() throws IOException
    {
        jsh.eval("echo abc \"|\" def", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("abc | def", output);
    }

    @Test
    public void test_nested_mixed_quotes() throws IOException
    {
        jsh.eval("echo \"a'b'c\"", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("a'b'c", output);
    }

    @Test
    public void test_nested_mixed_quotes_2() throws IOException
    {
        jsh.eval("echo 'a\"b\"c'", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("a\"b\"c", output);
    }

    @Test
    public void test_mixed_quotes() throws IOException
    {
        jsh.eval("echo a\"b\"c'd'e", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("abcde", output);
    }

}

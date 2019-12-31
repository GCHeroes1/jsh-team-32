package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullOutputStream;
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
                "dir1" + File.separator + "file3.txt",
                "dir1" + File.separator + "longfile.txt"};
        Arrays.sort(expected);

        String[] output = outputstr.split("\\s");
        Arrays.sort(output);

        assertArrayEquals(expected, output);
    }


    //======================================


    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
    public void test_non_interactive_shell_bad_arg() throws IOException {
        String[] args = new String[]{"ls", "badDir"};
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        PrintStream p = new PrintStream(stderr, true, StandardCharsets.UTF_8);
        System.setErr(p);

        Jsh.main(args);

        String data = new String(stderr.toByteArray(), StandardCharsets.UTF_8);
        data = data.strip();
        assertEquals("jsh: ls: unexpected argument", data);
    }

    @Test
    public void test_non_interactive_shell_too_many_args() throws IOException {
        String[] args = new String[]{"-c", "ls", "badDir"};
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        PrintStream p = new PrintStream(stderr, true, StandardCharsets.UTF_8);
        System.setErr(p);

        Jsh.main(args);

        String data = new String(stderr.toByteArray(), StandardCharsets.UTF_8);
        data = data.strip();
        assertEquals("jsh: wrong number of arguments", data);
    }

    @Test
    public void test_non_interactive_shell_too_few_args() throws IOException {
        String[] args = new String[]{"-c"};
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        PrintStream p = new PrintStream(stderr, true, StandardCharsets.UTF_8);
        System.setErr(p);

        Jsh.main(args);

        String data = new String(stderr.toByteArray(), StandardCharsets.UTF_8);
        data = data.strip();
        assertEquals("jsh: wrong number of arguments", data);
    }

    @Test
    public void test_non_interactive_shell() throws IOException {
        String[] args = new String[]{"-c", "ls dir1"};
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PrintStream p = new PrintStream(stdout, true, StandardCharsets.UTF_8);
        System.setOut(p);
        Jsh.main(args);
        String data = new String(stdout.toByteArray(), StandardCharsets.UTF_8);
        data = data.strip();

        String[] expected = new String[]{
                "file1.txt",
                "file2.txt",
                "file3.txt",
                "longfile.txt"
        };
        Arrays.sort(expected);

        String[] output = data.split("\r\n|\n|\t");
        Arrays.sort(output);

        assertArrayEquals(expected, output);
    }

    @Test
    public void test_non_interactive_shell_exception() throws IOException {
        String[] args = new String[]{"-c", "ls dir3"};
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        PrintStream p = new PrintStream(stderr, true, StandardCharsets.UTF_8);
        System.setErr(p);

        Jsh.main(args);

        String data = new String(stderr.toByteArray(), StandardCharsets.UTF_8);
        data = data.strip();
        assertEquals("jsh: ls: no such directory", data);
    }

    @Test
    public void test_blank() throws IOException {
        jsh.eval(" ", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("", output);
    }

    @Test
    public void test_semicolon() throws IOException {
        thrown.expect(RuntimeException.class);
        jsh.eval(";", out);
    }

    @Test
    public void test_double_space() throws IOException {
        jsh.eval("echo  foo", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("foo", output);
    }

    @Test(timeout = 2000)
    public void test_interactive_shell() throws IOException {
        ByteArrayInputStream input_stream = new ByteArrayInputStream("ls\nexit".getBytes());
        PrintStream output_stream = new PrintStream(new NullOutputStream());
        System.setIn(input_stream);
        System.setOut(output_stream);
        Jsh.main(new String[0]);
    }

    @Test(timeout = 2000)
    public void test_interactive_shell_ls_exception() throws IOException {
        ByteArrayInputStream input_stream = new ByteArrayInputStream("ls dir3\nexit".getBytes());

        ByteArrayOutputStream err_stream = new ByteArrayOutputStream();
        PrintStream stderr = new PrintStream(err_stream, true, StandardCharsets.UTF_8);
        PrintStream stdout = new PrintStream(new NullOutputStream());
        System.setIn(input_stream);
        System.setOut(stdout);
        System.setErr(stderr);

        Jsh.main(new String[0]);

        String err_str = new String(err_stream.toByteArray());
        err_str = err_str.strip();

        assertEquals("jsh: ls: no such directory", err_str);
    }
}

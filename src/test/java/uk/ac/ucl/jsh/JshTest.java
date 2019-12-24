package uk.ac.ucl.jsh;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.ArrayList;
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
    public void test_echo() {
        try {
            jsh.eval("echo hello world", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("hello world", output);
    }

    @Test
    public void test_ls() {
        try {
            jsh.eval("ls", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertArrayEquals(new String[]{"dir1", "dir2", "test.txt"}, output.split("[\n\t]"));
    }

    @Test
    public void test_ls_dir() {
        try {
            jsh.eval("ls dir1", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertArrayEquals(new String[]{"file1.txt", "file2.txt", "longfile.txt"}, output.split("[\n\t]"));
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
    public void test_pwd() throws IOException {
        try {
            jsh.eval("pwd", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals(workingDir.getCanonicalPath(), output);
    }

    @Test
    public void test_cd_pwd() throws IOException {
        try {
            jsh.eval("cd dir1; pwd", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals(workingDir.getCanonicalPath() + File.separator + "dir1", output);
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
    public void test_cat_stdin() {
        try {
            jsh.eval("cat < dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"AAA", "BBB", "AAA"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_head() {
        try {
            jsh.eval("head dir1/longfile.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"1", "2", "3", "4", "5", "6",
                "7", "8", "9", "10"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_head_stdin() {
        try {
            jsh.eval("head < dir1/longfile.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"AAA", "BBB", "AAA"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_head_n5() {
        try {
            jsh.eval("head -n 5 dir1/longfile.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"1", "2", "3", "4", "5"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_head_n50() {
        try {
            jsh.eval("head -n 50 dir1/longfile.txt", out);
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
    public void test_head_n0() {
        try {
            jsh.eval("head -n 0 dir1/longfile.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("", output);
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

    @Test
    public void test_grep()  {
        try {
            jsh.eval("grep AAA dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"AAA", "AAA"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_grep_no_match()  {
        try {
            jsh.eval("grep DDD dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("", output);
    }

    @Test
    public void test_grep_re()  {
        try {
            jsh.eval("grep 'A..' dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"AAA", "AAA"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_grep_files()  {
        try {
            jsh.eval("grep '...' dir1/file1.txt dir1/file2.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"AAA", "BBB",
                "AAA", "CCC"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_grep_stdin()  {
        try {
            jsh.eval("cat dir1/file1.txt dir1/file2.txt | grep '...'", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"AAA", "BBB",
                "AAA", "CCC"}, output.split("\r\n|\n"));
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
            jsh.eval("sed 's|A|D|'  dir1/file1.txt", out);
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
            jsh.eval("sed 's/A/D/g'  dir1/file1.txt", out);
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
            jsh.eval("sed 's/../DD/g'  dir1/file1.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"DDA", "DDB", "DDA"}, output.split("\r\n|\n"));
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
    public void test_find_dir()  {
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

    @Test
    public void test_input_redirection()  {
        try {
            jsh.eval("cat < dir1/file2.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("CCC", output);
    }

    @Test
    public void test_input_redirection_in_front()  {
        try {
            jsh.eval("< dir1/file2.txt cat", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("CCC", output);
    }

    @Test
    public void test_input_redirection_no_space()  {
        try {
            jsh.eval("cat <dir1/file2.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals("CCC", output);
    }

    @Test
    public void test_output_redirection() throws IOException {
        try {
            jsh.eval("echo foo > newfile.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }

        StringBuilder filecontent = new StringBuilder();
        try {
            File outputfile = new File("newfile.txt");
            BufferedReader bfr = new BufferedReader(new FileReader(outputfile));
            String line;
            while ((line = bfr.readLine()) != null) {
                filecontent.append(line);
            }
        } catch (FileNotFoundException e) {
            fail("File not created");
        }
        assertEquals("foo", filecontent.toString());
    }

    @Test
    public void test_output_redirection_overwrite() throws IOException {
        try {
            jsh.eval("echo foo > test.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }

        StringBuilder filecontent = new StringBuilder();
        try {
            File outputfile = new File("test.txt");
            BufferedReader bfr = new BufferedReader(new FileReader(outputfile));
            String line;
            while ((line = bfr.readLine()) != null) {
                filecontent.append(line);
            }
        } catch (FileNotFoundException e) {
            fail("File not created");
        }
        assertEquals("foo", filecontent.toString());
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
        try {
            jsh.eval("echo dir1/*.txt", out);
        } catch (Exception e) {
            fail(e.toString());
        }
        String outputstr = new String(out.toByteArray());
        outputstr = outputstr.strip();

        String[] expected = new String[]{"dir1/file1.txt",
                "dir1/file2.txt", "dir1/longfile.txt"};
        Arrays.sort(expected);

        String[] output = outputstr.split("\\s");
        Arrays.sort(output);

        assertArrayEquals(expected, output);
    }


//    @After
//    public void remove_test_files() throws IOException
//    {
//        FileUtils.cleanDirectory(new File("."));
//        System.out.println("hi");
//    }


}

package uk.ac.ucl.jsh;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class JshTest {
    private Jsh jsh;

    public JshTest()
    {
        jsh = new Jsh(System.getProperty("user.dir"));
    }



    @Test
    public void test_echo() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("echo hello world", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertEquals("hello world", output);
    }

    @Test
    public void test_ls() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("ls", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertArrayEquals(new String[]{"dir1", "dir2", "test.txt"}, output.split("[\n\t]"));
    }

    @Test
    public void test_ls_dir() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("ls dir1", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        //Scanner scn = new Scanner(in);
        assertArrayEquals(new String[]{"file1.txt", "file2.txt", "longfile.txt"}, output.split("[\n\t]"));
    }

    @Test
    public void test_ls_hidden() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("ls dir2/subdir", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        assertArrayEquals(new String[]{"file.txt"}, output.split("[\n\t]"));
    }

    @Test
    public void test_pwd() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("pwd", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals(System.getProperty("user.dir"), output);
    }

    @Test
    public void test_cd_pwd() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("cd dir1; pwd", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        assertEquals(System.getProperty("user.dir") + File.separator + "dir1", output);
    }

    @Test
    public void test_cat() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("cat dir1/file1.txt dir1/file2.txt", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"AAA", "BBB", "AAA", "CCC"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_cat_stdin() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("cat < dir1/file1.txt", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"AAA", "BBB", "AAA"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_head() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("head dir1/longfile.txt", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"1", "2", "3", "4", "5", "6",
                                        "7", "8", "9", "10"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_head_stdin() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("head < dir1/longfile.txt", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"AAA", "BBB", "AAA"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_head_n5() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("head -n 5 dir1/longfile.txt", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"1", "2", "3", "4", "5"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_head_n50() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("head -n 50 dir1/longfile.txt", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"1", "2", "3", "4", "5",
                "6", "7", "8", "9", "10", "11", "12", "13", "14",
                "15", "16", "17", "18", "19", "20"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_head_n0() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("head -n 0 dir1/longfile.txt", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertEquals("", output);
    }

    @Test
    public void test_tail() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("tail dir1/longfile.txt", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"11", "12", "13", "14", "15", "16",
                "17", "18", "19", "20"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_tail_stdin() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("tail < dir1/longfile.txt", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"11", "12", "13", "14", "15", "16",
                "17", "18", "19", "20"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_tail_n5() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("tail -n 5 dir1/longfile.txt", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"16", "17",
                "18", "19", "20"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_tail_n50() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("tail -n 50 dir1/longfile.txt", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertArrayEquals(new String[]{"1", "2", "3", "4", "5",
                "6", "7", "8", "9", "10", "11", "12", "13", "14",
                "15", "16", "17", "18", "19", "20"}, output.split("\r\n|\n"));
    }

    @Test
    public void test_tail_n0() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsh.eval("tail -n 0 dir1/longfile.txt", out);
        String output = new String(out.toByteArray());
        output = output.strip();
        // can be just \n, but added \r\n because a pleb is using windows...
        assertEquals("", output);
    }



}

package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

interface CommandInterface
{
    void run(String command, InputStream input, OutputStream output) throws IOException;
}

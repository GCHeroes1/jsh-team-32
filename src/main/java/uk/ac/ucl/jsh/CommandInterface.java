package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

interface CommandInterface
{
    void run(String input, OutputStream output) throws IOException;
}

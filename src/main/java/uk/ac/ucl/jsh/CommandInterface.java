package uk.ac.ucl.jsh;

import java.io.OutputStream;
import java.util.List;

interface CommandInterface
{
    void accept(List<String> input_cmds);

    void eval(List<String> input, OutputStream output);
}

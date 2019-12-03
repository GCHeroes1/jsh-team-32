package uk.ac.ucl.jsh;

interface CommandInterface
{
    void accept(String input_string);

    void eval(String input, String output);
}

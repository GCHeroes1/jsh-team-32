# Jsh Team 32
This repository refactors and extends the legacy JSH shell.

##JSH
The following are the classes that JSH is built with:
##### Jsh
Jsh is the main class. The program enters in the PSVM located in this class, and the main function calls either the
interactive shell, or if the `-c` flag was passed, executes whatever command is after said `-c` flag on the command
line.


##### Sequence
* ###### Semicolon
    When executing a command, Sequence is the first class that the code enters. Sequence splits the given string into 
    multiple commands if there are more than one command separated by a semicolon (as according to specification).
    
After splitting commands, each command is passed to the Pipe class as a String.
##### Pipe
Pipe takes a String command, and splits into multiple commands if there is at least one pipe operator (that isn't
inside quotes).

Pipe will then create appropriate input and output streams for each of the command in order to pass the outputs of
one command into the input of another; Pipe will then print to the original output stream once all commands have
been executed.

Once the commands are split, each command is sent to the Call class.
##### Call
Call receives a String as the command, as well as an input stream and an output stream.
* ###### Substitution
    Command substitution is first performed on the given String.
* ###### Quoting
    A method in Call splits the resulting string given by the command substitution is then split into tokens in such a way that quoted 
    arguments stay as a single token, while the rest of the tokens are space-delimited.
* ###### IO redirection
    A method in Call then scans trough the tokens looking for redirection operators. When found, the input stream
    and output stream passed to Call gets overridden by FileInputStream or/and FileOutputStream, depending on the
    given redirection operator.
    The tokens corresponding to IO redirection are then removed from the token array.
* ###### Globbing
    Another method in Call scans through the tokens again and tries to replace all arguments that contain a * that 
    is also unquoted (a glob pattern) to a file that matches the pattern in the present working directory. All
    quoted content is ignored. 
* ###### Quoting (pass 2)
    Once IO redirection and globbing have been performed, a different method from the first quoting method will then
    remove quotes from the input arguments, where they aren't inside other quotes (in which case they are kept). 

##Shell programs
####Cat
Built according to specification
####cd
Added support for absolute paths. The specification was ambiguous about whether or not absolute paths is supported. 
####echo
Built according to specification
####find
The specification calls for printing of relative paths; however we felt like adhering to the POSIX-defined behaviour
of `find`.
- When no search path is specified, print the relative path to the present working directory, prefixed with `./`.
- When there is a relative path specified, print with the relative path prefixed to the rest of the path as it was 
typed in the command line
    - Paths that contain circular routes such as `dir1/../dir1` will remain as such when printed
- We added support for absolute paths, and the output will print the absolute path; even when the absolute path
given points to a directory that is the same, or that is a subdirectory, of the present working directory, the
absolute path will be printed anyway.

    ![Example image](https://cdn.discordapp.com/attachments/622838479608348672/663238382864039946/unknown.png)
    
####grep
Built according to specification
####head
Built according to specification
####ls
Built according to specification
####pwd
Built according to specification
####sed
Built according to specification
####Tail
Built according to specification
####Wc
Built according to specification
___    
#### SPFactory
SPFactory is a factory class that creates instances of ShellPrograms for the shell to execute.


# Jsh Team 32
This repository refactors and extends the legacy JSH shell.
##Shell programs
* ##### Cat
    Built according to specification
* ##### cd
    Added support for absolute paths. The specification was ambiguous about whether or not absolute paths is supported. 
* ##### echo
    Built according to specification
* ##### find
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
    
* ##### grep
Built according to specification
* ##### head
Built according to specification
* ##### IO redirection
Built according to specification
* ##### ls
Built according to specification
* ##### pwd
Built according to specification
* ##### sed
Built according to specification
* ##### shellProgram
* ##### SPFactory
* ##### Tail
Built according to specification
* ##### Wc
Built according to specification
* ##### Call
Built according to specification
* ##### Semicolon
Built according to specification
* ##### Substitution
Built according to specification
* ##### Pipe
Built according to specification
* ##### Sequence
Built according to specification
* ##### Jsh
Built according to specification
* ##### Quoting
Built according to specification


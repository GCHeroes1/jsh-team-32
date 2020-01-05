# Team 32
## Jsh: legacy shell
#####Cat
didnt change anything, exactly to spec
#####cd
supports absolute directories as it was ambiguous as to whether it was supposed to 
#####echo
didnt change anything, exactly to spec
#####find
had some pretty odd behaviour that wasnt in the spec that was implemented, find has 3 different 
way of printing prefix things
on the first example, we dont specify a path so it'll use the current directory, when it uses the
current directory, its gonna prefix the results with a "./", which is the POSIX way of saying
its the current directory. second example, when you do specify a path, if you give it a relative
path, relative to the current directory, it will not prefix with a "./". third example, if you 
give it a path that contains something which undoes itself "dir1/../dir1", it will not try and 
resolve the path and just keep it as typed. on the 4th example, when you use absolute path
it will print the absolute path, even if it points to something that is in the current 
directory.  
#####grep
didnt change anything, exactly to spec
#####head
didnt change anything, exactly to spec
##### IO redirection
didnt change anything, exactly to spec
#####ls
didnt change anything, exactly to spec
#####pwd
didnt change anything, exactly to spec
#####sed
didnt change anything, exactly to spec
#####shellProgram
#####SPFactory
#####Tail
didnt change anything, exactly to spec
#####Wc
didnt change anything, exactly to spec
#####Call
didnt change anything, exactly to spec
#####Semicolon
didnt change anything, exactly to spec
#####Substitution
didnt change anything, exactly to spec
#####Pipe
didnt change anything, exactly to spec
#####Sequence
didnt change anything, exactly to spec
#####Jsh
didnt change anything, exactly to spec
#####Quoting
didnt change anything, exactly to spec

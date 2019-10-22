package uk.ac.ucl.jsh;

import java.util.regex.Matcher;
import java.util.regex.Pattern;  
  
public class RegexGroupExample2 {  
    public static void main(String[] args) {  
        Pattern pattern = Pattern.compile("(i)(s)(i)(s)");  
          String input = "isisisicdcadsfsdffsdsisisisis";  
          Matcher m = pattern.matcher(input);  
          m.find();  
          String grp0 = m.group(0);  
          String grp1 = m.group(1);  
          String grp2 = m.group(2); 
          String grp3 = m.group(3);  

          System.out.println("Group 0 " + grp0);  
          System.out.println("Group 1 " + grp1);
          System.out.println("Group 2 " + grp2); 
          System.out.println("Group 3 " + grp3);   

           
          System.out.println(input);  
        }  
    }  
package com.pri.secfg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecConfig
{
 private static class Var
 {
  private String name;
  String value;
  List<String> values;
 }
 
 private static Pattern secPtt = Pattern.compile("\\s*\\[\\s*(.*)\\s*\\]\\s*");
 private static Pattern valPtt = Pattern.compile("\\s*(.*)\\s*=\\s*(.*)\\s*");
 
 private Map< String, Map<String, Var> > conf = new HashMap<String, Map<String,Var>>();
 
 public void read( Reader rd ) throws IOException, ConfigException
 {
  BufferedReader bfrd = null;
  
  if( rd instanceof BufferedReader )
   bfrd = (BufferedReader)rd;
  else
   bfrd = new BufferedReader(rd);
  
  Matcher secM = secPtt.matcher("");
  Matcher varM = valPtt.matcher("");
  
  int ln=0;
  String line = null;
  while( ( line = bfrd.readLine() ) != null )
  {
   ln++;
   
   varM.reset(line);
   
   if( varM.matches() )
   {
    String secName = varM.group(1);
    
    if( conf.containsKey(secName) )
     throw new ConfigException("Line "+ln+": section exists");
    
   }
   
  }
  
 }
}

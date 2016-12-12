package com.pri.secfg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecConfig
{
 private static Pattern secPtt = Pattern.compile("\\s*\\[\\s*(.*)\\s*\\]\\s*");
 private static Pattern valPtt = Pattern.compile("\\s*([^\\[][^=]*(?<=\\S))\\s*(?:=\\s*(.*\\S)\\s*)?");
 private static Pattern commPtt = Pattern.compile("\\s*#.*");
 
 private Map< String, Section > conf = new LinkedHashMap<String, Section>();
 
 public void read( Reader rd ) throws IOException, ConfigException
 {
  BufferedReader bfrd = null;
  
  if( rd instanceof BufferedReader )
   bfrd = (BufferedReader)rd;
  else
   bfrd = new BufferedReader(rd);
  
  Matcher secM = secPtt.matcher("");
  Matcher varM = valPtt.matcher("");
  Matcher commM = commPtt.matcher("");
  
  int ln=0;
  String line = null;
  
  Section csec=null;
  
  while( ( line = bfrd.readLine() ) != null )
  {
   ln++;
   
   line = line.trim();
   
   if( line.length() == 0 )
    continue;
   
   commM.reset(line);
   
   if( commM.matches() )
    continue;
   
   
   varM.reset(line);
   
   if( varM.matches() )
   {
    String varName = varM.group(1);
    String varVal  = varM.group(2);
    
    Var exVar = null;
    
    if( csec == null )
    {
     csec = new Section(null);
     conf.put(null, csec);
    }
    else
     exVar = csec.getVariable(varName);
    
    if( exVar == null )
    {
     exVar = new Var(varName,varVal);
     csec.addVariable(exVar);
    }
    else
     exVar.addValue(varVal);
   
    continue;
   }
   
   secM.reset(line);
   
   if( secM.matches() )
   {
    String secName = secM.group(1);
    
    if( conf.containsKey(secName) )
     throw new ConfigException("Line "+ln+": section exists");
    
    csec = new Section(secName);
    
    conf.put(secName, csec);
    
    continue;
   }
   
   throw new ConfigException("Line "+ln+": invalid syntax");
   
  }
  
 }

 public Section getSection( String nm )
 {
  return conf.get(nm);
 }
 
 public Collection<Section> getSections()
 {
  return conf.values();
 }
}

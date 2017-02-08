package com.pri.secfg;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Section
{
 private String name;
 private Map<String, Var> vars = new LinkedHashMap<String, Var>();
 
 public Section()
 {
 }

 public Section(String nm)
 {
  name = nm;
 }

 public void addVariable( Var v )
 {
  vars.put(v.getName(),v);
 }
 
 public Var getVariable( String n )
 {
  return vars.get(n);
 }
 
 public Collection<Var> getVariables()
 {
  return vars.values();
 }
 
 public String getName()
 {
  return name;
 }

 public void setName(String name)
 {
  this.name = name;
 }

 public String getValue(String string)
 {
  Var v = getVariable(string);
  
  if( v != null )
   return v.getValue();
  
  return null;
 }
 
}

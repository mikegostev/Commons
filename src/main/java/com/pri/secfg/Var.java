package com.pri.secfg;

import java.util.ArrayList;
import java.util.List;

public class Var
{
 private String name;
 String value;
 List<String> values;

 public Var(String varName, String varVal)
 {
  name = varName;
  value = varVal;
 }
 
 public void addValue(String varVal)
 {
  if( values == null )
   values = new ArrayList<String>(5);
 
  values.add(varVal);
  value = varVal;
 }

 public String getName()
 {
  return name;
 }

 public String getValue()
 {
  return value;
 }

 public List<String> getValues()
 {
  return values;
 }
}
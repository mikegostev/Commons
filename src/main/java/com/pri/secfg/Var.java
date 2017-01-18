package com.pri.secfg;

import java.util.ArrayList;
import java.util.Collections;
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
  {
   values = new ArrayList<String>(5);
   values.add(value);
  }
  
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
  if( values == null )
   return Collections.singletonList(value);
  
  return values;
 }

 public int getValuesCount()
 {
  if( values == null )
   return 1;
  
  return values.size();
 }
}
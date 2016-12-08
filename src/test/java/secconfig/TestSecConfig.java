package secconfig;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.pri.secfg.ConfigException;
import com.pri.secfg.SecConfig;
import com.pri.secfg.Section;
import com.pri.secfg.Var;

public class TestSecConfig
{

 public static void main(String[] args) throws FileNotFoundException, IOException, ConfigException
 {
  SecConfig cfg = new SecConfig();
  
  cfg.read( new FileReader("/dev/tmp/test.config") );
  
  
  for( Section sec : cfg.getSections() )
  {
   System.out.println("\nSection: "+(sec.getName()==null?"<default>":sec.getName())+"\n\n");
   
   for( Var v : sec.getVariables() )
    System.out.println(v.getName()+" + "+v.getValue() );
   
  }
  
  System.out.println();
  
 }
 
}

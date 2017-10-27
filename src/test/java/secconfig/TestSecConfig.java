package secconfig;

import com.pri.secfg.ConfigException;
import com.pri.secfg.SecConfig;
import com.pri.secfg.Section;
import com.pri.secfg.Value;
import com.pri.secfg.Var;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TestSecConfig {

    public static void main(String[] args) throws FileNotFoundException, IOException, ConfigException {
        SecConfig cfg = new SecConfig();

        cfg.read(new FileReader("/dev/tmp/test.config"));

        for (String s : args) {
            cfg.resetVariable(s);
        }

        for (Section sec : cfg.getSections()) {
            System.out.println("\nSection: " + (sec.getName() == null ? "<default>" : sec.getName()) + "\n\n");

            for (Var v : sec.getVariables()) {
                String val = v.getStringValue();

                if (v.getValuesCount() > 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append('[');

                    for (Value vl : v.getValues()) {
                        sb.append(vl.getStringValue()).append(",");
                    }

                    sb.setCharAt(sb.length() - 1, ']');

                    val = sb.toString();
                }

                System.out.println("'" + v.getName() + "' = '" + val + "'");
            }
        }

        System.out.println();

    }

}

package com.pri.secfg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecConfig implements VarSet {

    private static Pattern secPtt = Pattern.compile("\\s*\\[\\s*(.*)\\s*\\]\\s*");
    private static Pattern valPtt = Pattern.compile("\\s*([^\\[][^=]*(?<=\\S))\\s*(?:=\\s*(.*\\S)\\s*)?");
    private static Pattern commPtt = Pattern.compile("\\s*#.*");
    private static Pattern sepPtt = Pattern.compile("\\s+");

    private List<Section> conf = new ArrayList<Section>();
    // private Map< String, Section > conf = new LinkedHashMap<String, Section>();
    private Section defSection;

    public void read(Reader rd) throws IOException, ConfigException {
        BufferedReader bfrd = null;

        if (rd instanceof BufferedReader) {
            bfrd = (BufferedReader) rd;
        } else {
            bfrd = new BufferedReader(rd);
        }

        Matcher secM = secPtt.matcher("");
        Matcher varM = valPtt.matcher("");
        Matcher commM = commPtt.matcher("");

        int ln = 0;
        String line = null;

        Section csec = null;

        while ((line = bfrd.readLine()) != null) {
            ln++;

            line = line.trim();

            if (line.length() == 0) {
                continue;
            }

            commM.reset(line);

            if (commM.matches()) {
                continue;
            }

            varM.reset(line);

            if (varM.matches()) {
                String varName = varM.group(1);
                String varVal = varM.group(2);

                String[] parts = sepPtt.split(varName);

                if (parts.length > 1) {
                    varName = String.join(" ", parts);
                }

                Var exVar = null;

                if (csec == null) {
                    csec = new Section(null);
//     conf.add(csec);
                    defSection = csec;
                } else {
                    exVar = csec.getVariable(varName);
                }

                if (exVar == null) {
                    exVar = new Var(varName, varVal, ln);
                    csec.addVariable(exVar);
                } else {
                    exVar.addValue(varVal, ln);
                }

                continue;
            }

            secM.reset(line);

            if (secM.matches()) {
                String secName = secM.group(1);

//    if( conf.containsKey(secName) )
//     throw new ConfigException("Line "+ln+": section exists");

                csec = new Section(secName);

                conf.add(csec);

                continue;
            }

            throw new ConfigException("Line " + ln + ": invalid syntax");

        }

    }


    public void resetVariable(String varNameVal) throws ConfigException {
        Matcher varM = valPtt.matcher(varNameVal);

        if (varM.matches()) {
            resetVariable(varM.group(1), varM.group(2));
        } else {
            throw new ConfigException("Line '" + varNameVal + "': invalid syntax");
        }
    }

    public void resetVariable(String varName, String val) {
        String[] parts = sepPtt.split(varName);

        if (parts.length > 1) {
            varName = String.join(" ", parts);
        }

        if (defSection == null) {
            defSection = new Section(null);
        }

        defSection.addVariable(new Var(varName, val, 0));
    }

    public Section getSection(String nm) {
        for (Section s : conf) {
            if (nm.equals(s.getName())) {
                return s;
            }
        }

        return null;
    }

    public Collection<Section> getSections() {
        return conf;
    }

    @Override
    public Var getVariable(String n) {
        if (defSection == null) {
            return null;
        }

        return defSection.getVariable(n);
    }

    @Override
    public Collection<Var> getVariables() {
        if (defSection == null) {
            return null;
        }

        return defSection.getVariables();
    }

    public String getStringValue(String nm) {
        if (defSection == null) {
            return null;
        }

        Var v = defSection.getVariable(nm);

        if (v == null) {
            return null;
        }

        return v.getStringValue();
    }


    @Override
    public Value getValue(String nm) {
        if (defSection == null) {
            return null;
        }

        Var v = defSection.getVariable(nm);

        if (v == null) {
            return null;
        }

        return v.getValue();
    }
}

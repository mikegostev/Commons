package com.pri.secfg;

import java.util.Collection;

public interface VarSet {

    String getStringValue(String nm);

    Value getValue(String nm);

    Var getVariable(String n);

    Collection<Var> getVariables();
}

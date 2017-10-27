package com.pri.adob;

public interface MutableADOB extends ADOB {

    void setName(String name);

    void setFileName(String name);

    void setTypeParameter(String prm, String val);

    void setDispositionParameter(String prm, String val);

    void setMetaInfo(Object meta);

    void setDisposition(String disp);

    void setContentType(String mimTyp);

    void setContentID(String mimeid);

    void setMetaSerialized(boolean metaSerialized);

}
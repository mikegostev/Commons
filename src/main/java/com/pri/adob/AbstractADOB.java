package com.pri.adob;

import com.pri.mail.MimeString;
import com.pri.mail.MimeType;
import com.pri.mail.MimeTypeParseException;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractADOB implements MutableADOB {

    public final static MimeType binaryContentType;

    static {
        MimeType mt = null;
        try {
            mt = new MimeType("application", "octet-stream");
        } catch (MimeTypeParseException e) {
        }

        binaryContentType = mt;
    }

    protected MimeType mimeType;
    protected MimeString mimeDisposition;
    protected String mimeID;
    protected Object meta;
    protected boolean metaSerialized = false;

    public abstract byte[] getContent() throws IOException;

    public abstract InputStream getInputStream() throws IOException;

    public AbstractADOB(String mimeType) {
        this(mimeType, null, null, false);
    }

    public AbstractADOB(String mimTyp, String disp, Object meta, boolean metaSeri) {
        if (mimTyp != null) {
            try {
                mimeType = new MimeType(mimTyp);
            } catch (MimeTypeParseException e) {
                mimeType = binaryContentType;
            }
        } else {
            mimeType = binaryContentType;
        }

        try {
            if (disp != null) {
                mimeDisposition = new MimeString(disp);
            }
        } catch (MimeTypeParseException e) {
        }

        this.meta = meta;
        metaSerialized = metaSeri;
    }

    public String getContentType() {
        return mimeType.toString();
    }

    public abstract long getContentSize();

    public String getCharset() {
        return mimeType.getParameter("charset");
    }

    public String getPrimaryType() {
        if (mimeType == null) {
            return null;
        }

        return mimeType.getPrimaryType();
    }

    public String getSubType() {
        if (mimeType == null) {
            return null;
        }

        return mimeType.getSubType();
    }

    public String getName() {
        return mimeType.getParameter("name");
    }

    public void setName(String name) {
        mimeType.setParameter("name", name);
    }

    public String getFileName() {
        if (mimeDisposition == null) {
            return null;
        }

        return mimeDisposition.getParameter("filename");
    }

    public void setFileName(String name) {
        if (mimeDisposition != null) {
            mimeDisposition.setParameter("filename", name);
        } else {
            try {
                mimeDisposition = new MimeString("attachment; filename=\"" + name + '"');
            } catch (MimeTypeParseException e) {
            }
        }
    }

    public String getTypeParameter(String prm) {
        return mimeType.getParameter(prm);
    }

    public String getDispositionParameter(String prm) {
        if (mimeDisposition == null) {
            return null;
        }

        return mimeDisposition.getParameter(prm);
    }

    public void setTypeParameter(String prm, String val) {
        mimeType.setParameter(prm, val);
    }

    public void setDispositionParameter(String prm, String val) {
        if (mimeDisposition != null) {
            mimeDisposition.setParameter(prm, val);
        }
    }


    public String getDisposition() {
        return mimeDisposition != null ? mimeDisposition.toString() : null;
    }

    public String getPosition() {
        if (mimeDisposition == null) {
            return null;
        }

        return mimeDisposition.getBaseType();
    }

    public Object getMetaInfo() {
        return meta;
    }

    public void setMetaInfo(Object meta) {
        this.meta = meta;
    }

    public void setDisposition(String disp) {
        try {
            if (disp != null) {
                mimeDisposition = new MimeString(disp);
            }
        } catch (MimeTypeParseException e) {
        }

    }

    public void setContentType(String mimTyp) {
        try {
            mimeType = new MimeType(mimTyp);
        } catch (MimeTypeParseException e) {
            mimeType = binaryContentType;
        }

    }

    public String getContentID() {
        return this.mimeID;
    }

    public void setContentID(String mimeid) {
        this.mimeID = mimeid;
    }

    public boolean isMetaSerialized() {
        return metaSerialized;
    }

    public void setMetaSerialized(boolean metaSerialized) {
        this.metaSerialized = metaSerialized;
    }

    public void release() {
    }

    public static MutableADOB makeMutable(ADOB a) {
        if (a instanceof MutableADOB) {
            return (MutableADOB) a;
        }

        return new ProxyADOB(a);
    }
}

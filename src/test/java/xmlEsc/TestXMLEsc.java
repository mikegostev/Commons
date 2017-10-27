package xmlEsc;

import com.pri.util.StringUtils;

public class TestXMLEsc {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(StringUtils.xmlEscaped("abcd\023 - <> \\\"&"));

    }

}

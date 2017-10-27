package httpaccept;

import com.pri.util.HttpAccept;
import java.util.Arrays;
import java.util.List;

public class TestHttpAccept {

    public static void main(String[] args) {
        HttpAccept accp = new HttpAccept(
                "text/xml, application/xml, application/xhtml+xml, text/html;q=0.9,  text/plain;q=0.8, image/png,*/*;"
                        + "q=0.5");

        List<String> prb = Arrays.asList(new String[]{"text/xhtml", "text/php", "text/xxml"});

        int ind = accp.bestMatch(prb);

        String match = "none";

        if (ind >= 0) {
            match = prb.get(ind);
        }

        System.out.println("Best match: " + match);

    }

}

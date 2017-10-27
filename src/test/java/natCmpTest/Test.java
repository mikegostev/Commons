package natCmpTest;

import com.pri.util.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        List<String> lst = new ArrayList<String>() {{
            add("123");
            add("AAA");
            add("X-1");
            add("X-01");
            add("X-001");
            add("X-10");
            add("X-19");
            add("X-109");
            add("X-109-Y");
            add("X-01-Y");
            add("");
            add("X-31-Y11");
            add("X-01");
            add("X-31-Y2");
            add("X-31-Y1");
            add("X-1X");
            add("X-31-YZ");
            add("X-019");
        }};

        Collections.sort(lst);

        for (int i = 0; i < lst.size(); i++) {
            System.out.println(lst.get(i));
        }

        System.out.println("=====");

        Collections.sort(lst, new Comparator<String>() {

            @Override
            public int compare(String str1, String str2) {
                return StringUtils.naturalCompare(str1, str2);
            }
        });

        for (int i = 0; i < lst.size(); i++) {
            System.out.println(lst.get(i));
        }


    }

}

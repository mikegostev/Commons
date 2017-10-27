package listunion;

import com.pri.util.collection.ListUnion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class ListUnionTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        List<String> l1 = new ArrayList<String>(Arrays.asList("A1", "A2", "A3"));
        List<String> l2 = new ArrayList<String>(Arrays.asList("B4", "B5", "B6"));
        List<String> l3 = new ArrayList<String>(Arrays.asList("C7", "C8", "C9"));
        List<String> l4 = new ArrayList<String>(Arrays.asList("D10"));

        List<String> union = new ListUnion<String>(l1, l2, l3, l4);

        for (int i = 0; i < union.size(); i++) {
            System.out.println(union.get(i));
        }

        System.out.println("--------");

        for (String s : union) {
            System.out.println(s);
        }

        System.out.println("--------");

        ListIterator<String> lit = l1.listIterator(l1.size());

        while (lit.hasPrevious()) {
            System.out.println(lit.previous());
        }

        System.out.println("--------");

        lit = union.listIterator(union.size());

        while (lit.hasPrevious()) {
            System.out.println(lit.previous());
        }

        System.out.println("--------");

        lit = union.listIterator();

        String el = null;
        for (int i = 0; i < 10; i++) {
            System.out.println(
                    lit.hasNext() + " el=" + el + " prev(" + lit.previousIndex() + ")=" + (lit.previousIndex() >= 0
                            ? union.get(lit.previousIndex()) : "?") + " next(" + lit.nextIndex() + ")=" + (
                            lit.nextIndex() < union.size() ? union.get(lit.nextIndex()) : "?"));

            el = lit.next();

        }

        System.out.println(
                lit.hasNext() + " el=" + el + " prev(" + lit.previousIndex() + ")=" + (lit.previousIndex() >= 0 ? union
                        .get(lit.previousIndex()) : "?") + " next(" + lit.nextIndex() + ")=" + (
                        lit.nextIndex() < union.size() ? union.get(lit.nextIndex()) : "?"));

        System.out.println("--------");

//  lit = union.listIterator(union.size());

        el = null;
        for (int i = 0; i < 10; i++) {
            System.out.println(
                    lit.hasPrevious() + " el=" + el + " prev(" + lit.previousIndex() + ")=" + (lit.previousIndex() >= 0
                            ? union.get(lit.previousIndex()) : "?") + " next(" + lit.nextIndex() + ")=" + (
                            lit.nextIndex() < union.size() ? union.get(lit.nextIndex()) : "?"));

            el = lit.previous();

        }

        System.out.println(
                lit.hasPrevious() + " el=" + el + " prev(" + lit.previousIndex() + ")=" + (lit.previousIndex() >= 0
                        ? union.get(lit.previousIndex()) : "?") + " next(" + lit.nextIndex() + ")=" + (
                        lit.nextIndex() < union.size() ? union.get(lit.nextIndex()) : "?"));

        System.out.println("--------");

        el = null;
        for (int i = 0; i < 10; i++) {
            System.out.println(
                    lit.hasNext() + " el=" + el + " prev(" + lit.previousIndex() + ")=" + (lit.previousIndex() >= 0
                            ? union.get(lit.previousIndex()) : "?") + " next(" + lit.nextIndex() + ")=" + (
                            lit.nextIndex() < union.size() ? union.get(lit.nextIndex()) : "?"));

            el = lit.next();

        }

        System.out.println(
                lit.hasNext() + " el=" + el + " prev(" + lit.previousIndex() + ")=" + (lit.previousIndex() >= 0 ? union
                        .get(lit.previousIndex()) : "?") + " next(" + lit.nextIndex() + ")=" + (
                        lit.nextIndex() < union.size() ? union.get(lit.nextIndex()) : "?"));

        System.out.println("--------");

        lit = union.listIterator();
        for (int i = 0; i < 3; i++) {
            System.out.println("next=" + lit.next());
        }

        lit.hasNext();

        System.out.println("prev=" + lit.previous());

        System.out.println("--------");

        for (String s : union.subList(3, 9)) {
            System.out.println(s);
        }

    }

}

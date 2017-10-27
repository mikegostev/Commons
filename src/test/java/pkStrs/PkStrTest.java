package pkStrs;

import uk.ac.ebi.mg.packedstring.PackedString;


public class PkStrTest {

    public static void main(String[] arg) {
        Object packed = PackedString.pack("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ");

        System.out.println(packed);
        System.out.println(packed.getClass().getName());
    }
}

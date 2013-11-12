package ch.ethz.syslab.telesto.common.util;

public class StringUtil {

    private StringUtil() {
    }

    public static String joinString(String glue, Object... parts) {
        StringBuilder sb = new StringBuilder();

        if (parts.length > 0) {
            sb.append(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                sb.append(glue);
                sb.append(parts[i]);
            }
        }

        return sb.toString();
    }
}

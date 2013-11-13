package ch.ethz.syslab.telesto.common.config;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class SortedProperties extends Properties {
    private static final long serialVersionUID = 1L;

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public synchronized Enumeration<Object> keys() {
        Enumeration<Object> keysEnum = super.keys();
        Vector keyList = new Vector();
        while (keysEnum.hasMoreElements()) {
            keyList.add(keysEnum.nextElement());
        }
        Collections.sort(keyList);
        return keyList.elements();
    }
}

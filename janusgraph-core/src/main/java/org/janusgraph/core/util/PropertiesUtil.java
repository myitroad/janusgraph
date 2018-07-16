package org.janusgraph.core.util;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Diablo on 2017/5/12.
 * 获取datasource的配置数据
 */
public class PropertiesUtil {
    public static Properties getProperties(String... properties) {
        Properties p = new Properties();
        for (String propertie : properties) {
            try {
                InputStream in = PropertiesUtil.class.getClassLoader()
                    .getResourceAsStream(propertie);
                p.load(new InputStreamReader(in, "UTF-8"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return p;
    }

    public static String getValue(String properties, String key) {
        Properties p = getProperties(properties);
        return p.getProperty(key);
    }
}
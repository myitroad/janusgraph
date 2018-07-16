package org.janusgraph.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by liutingna on 2018/7/16.
 *
 * @author liutingna
 */
public class AnalyzerUtil {
    private static Properties properties;
    static {
        try {
            properties = PropertiesUtil.getProperties("datasource.properties");
        } catch (Exception e) {
            System.err.print("加载应用URL配置文件错误：" + e.toString());
        }
    }

    public static Set<String> analyzeQueryString(String query) throws Exception{
        String[] hosts = properties.getProperty("cluster.httpAddresses").split("\\|");
        int httpPort = NumberUtils.toInt(properties.getProperty("cluster.httpPort"),9200);
        if (ArrayUtils.isEmpty(hosts)) {
            System.err.print("Cannot get ES url config: {}"+ hosts);
            return Collections.emptySet();
        }
        String url = "http://"+hosts[0] + ":" + httpPort + "/_analyze";
        NameValuePair[] nameValuePairs = new BasicNameValuePair[2];
        nameValuePairs[0] = new BasicNameValuePair("text", query);
        nameValuePairs[1] = new BasicNameValuePair("analyzer", "ik_max_word");
        String rstString = HttpUtil.handleHttpMethod(url, "post", nameValuePairs);
        JSONObject rstJson = JSON.parseObject(rstString);
        JSONArray array = rstJson.getJSONArray("tokens");
        Set<String> tokenSet = new HashSet<>();
        for (Object obj : array) {
            if (obj instanceof JSONObject) {
                JSONObject tokenJson = (JSONObject) obj;
                tokenSet.add(tokenJson.getString("token"));
            }
        }
        return tokenSet;
    }
}

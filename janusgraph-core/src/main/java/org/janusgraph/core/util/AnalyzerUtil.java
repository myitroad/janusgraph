package org.janusgraph.core.util;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by liutingna on 2018/7/16.
 *
 * @author liutingna
 */
public class AnalyzerUtil {

    public static Set<String> analyzeQueryString(String query) throws Exception {
        Set<String> tokens = new HashSet<>();
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<SegToken> list = segmenter.process(query, JiebaSegmenter.SegMode.SEARCH);
        for(int i = 0; i < list.size(); i++) {
            tokens.add(list.get(i).word);
        }
        return tokens;
    }

}

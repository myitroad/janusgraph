package org.janusgraph.core.util;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.util.List;

/**
 * Created by liutingna on 2018/7/16.
 *
 * @author liutingna
 */
public class AnalyserUtil {
    /**
     * 对查询字符串进行结巴分词,
     *
     * @param searchStr 查询字符串
     * @return 分词后的SegToken列表
     */
    public static List<SegToken> searchAnalyser(String searchStr) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<SegToken> list = segmenter.process(searchStr, JiebaSegmenter.SegMode.SEARCH);
        return list;
    }
}

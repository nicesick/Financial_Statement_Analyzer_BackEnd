package com.jihun.study.openDartApi.utils.parser;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

public class DartXmlParser {
    /**
     * parse
     *
     * 들어온 XML 파일이름과 태그속성을 이용해 xml 을 parsing 합니다.
     *
     * @param xml
     * @param tags
     *
     * @return xml parsing data 리스트
     *
     * @throws JDOMException
     * @throws IOException
     */
    public static List<Map<String, String>> parse(String xml, String[] tags) throws JDOMException, IOException {
        List<Map<String, String>> output = new ArrayList<>();

        SAXBuilder  saxBuilder  = new SAXBuilder();
        Document    document    = saxBuilder.build(xml);
        Element     docRoot     = document.getRootElement();

        List<Element> corpCodes = docRoot.getChildren("list");

        for (Element corpCode : corpCodes) {
            Map<String, String> corpCodeMap = new HashMap<>();

            for (String tag : tags) {
                corpCodeMap.put(tag, corpCode.getChildText(tag));
            }

            output.add(corpCodeMap);
        }

        return output;
    }
}

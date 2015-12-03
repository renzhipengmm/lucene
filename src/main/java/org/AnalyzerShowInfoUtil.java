package org;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

public class AnalyzerShowInfoUtil {
	public static void showAnalyzerInfo(String str, Analyzer a) {
		try {
			TokenStream s = a.tokenStream("", new StringReader(str));
			CharTermAttribute charTermAttribute = s.addAttribute(CharTermAttribute.class);
			// KeywordAttribute keywordAttribute =
			// s.addAttribute(KeywordAttribute.class);
			s.reset();
			while (s.incrementToken()) {
				System.out.print("[" + charTermAttribute.toString() + "]");
				// System.out.println(keywordAttribute.isKeyword());
			}
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

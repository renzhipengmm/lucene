package org;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.junit.Before;
import org.junit.Test;

public class TestAdvanceLucene {
	private static AdvancedLucene lucene;

	@Before
	public void init() {
		lucene = new AdvancedLucene();
	}

	@Test
	public void testCreateIndex() {
		System.out.println(lucene);
		lucene.createIndex("D:\\myluceneTextFile");
	}

	@Test
	public void showtDirectoryInfo() {
		lucene.getDirectoryIndexInfo();
	}

	@Test
	public void testQuery() {
		int x = 0;
		lucene.getDirectoryIndexInfo();
		while (x < 5) {
			lucene.queryBySearchManage("好", 100);
			if (x == 2) {
				lucene.deleteIndex("tomcat.txt");
			}
			x++;
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lucene.commitIndexChage();
		lucene.getDirectoryIndexInfo();

	}

	@Test
	public void testQueryFileName() {
		lucene.quertyByTermSearchManage("j2ee大纲解析.txt");
	}

	@Test
	public void showAnalyzerInfo() {
		try {
			String str = FileUtils.readFileToString(new File("D:\\myluceneTextFile\\j2ee大纲解析.txt"));
			SmartChineseAnalyzer a = new SmartChineseAnalyzer();
			AnalyzerShowInfoUtil.showAnalyzerInfo(str, a);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void deleteDoc() {
		lucene.deleteIndex("tomcat.txt");
	}

	@Test
	public void deletAll() {
		lucene.deleteAllIndex();
	}

	@Test
	public void testForceMergeDeletes() {
		lucene.forceMergeDeletes();
	}

}

package org;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.junit.Test;

public class TestIndexUtil {
	@Test
	public void testCreate() {
		IndexUtil util = new IndexUtil();
		util.createIndex("E:\\my");
	}

	@Test
	public void testInfo() {
		IndexUtil util = new IndexUtil();
		util.getDirectoryIndexInfo();
	}

	@Test
	public void testQuery() {
		IndexUtil util = new IndexUtil();
		util.queryIndex("public");
	}

	@Test
	public void testDeleteAll() {
		IndexUtil util = new IndexUtil();
		util.deleteAllIndex();
	}

	@Test
	public void testUnDeleteAll() {
		IndexUtil util = new IndexUtil();
		util.unDelete();
	}

	@Test
	public void testUpdate() {
		IndexUtil util = new IndexUtil();
		Document doc = new Document();
		doc.add(new StringField("name", "更新添加的新索引", Store.YES));
		doc.add(new TextField("context", "更新添加的新索引 ---------内容", Store.NO));
		doc.add(new LongField("size", 18, Store.YES));
		util.updateIndex(new Term("name", "某时间段内的周排序.java"), doc);
	}

	@Test
	public void testMergeDeletes() {
		IndexUtil util = new IndexUtil();
		util.forceMergeDeletes();
	}

	@Test
	public void testCreateBootIndex() {
		IndexUtil util = new IndexUtil();
		util.createBootIndex("E:\\my");
	}

	@Test
	public void testBootIndexQuery() {
		IndexUtil util = new IndexUtil();
		util.queryIndex("CREATE_SELECT");
	}

	@Test
	public void testFileEName() {
		String ffName = "教师评价数据验证.sql";
		System.out.println(FilenameUtils.getExtension(ffName));
	}

}

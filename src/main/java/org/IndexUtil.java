package org;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexUtil {
	private static Directory directory;
	private static Analyzer analyzer;
	private static IndexWriterConfig indexWriterConfig;
	private static IndexWriter indexWriter;

	private static DirectoryReader directoryReader;

	Logger looger = Logger.getLogger(this.getClass());

	private Map<String, Float> bootMap = new HashMap<String, Float>();

	public IndexUtil() {
		Date st = new Date();
		looger.info("初始化索引库....");
		try {
			directory = FSDirectory.open(Paths.get("E:" + File.separator + "lucene"));
			analyzer = new StandardAnalyzer();
			indexWriterConfig = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, indexWriterConfig);

			looger.info("初始化索引库完成,耗时[" + (new Date().getTime() - st.getTime()) + "]ms");

			bootMap.put("sql", 1.1f);
			bootMap.put("java", 1.5f);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void createIndex(String path) {
		File f = new File(path);
		if (f.isDirectory()) {
			for (File ff : f.listFiles()) {
				if (ff.getName().endsWith(".txt") || ff.getName().endsWith(".java") || ff.getName().endsWith(".sql")) {
					Date st = new Date();
					looger.info("开始创建[" + ff.getName() + "]索引.....");
					try {
						Document doc = new Document();
						doc.add(new StringField("name", ff.getName(), Store.YES));
						doc.add(new TextField("context", FileUtils.readFileToString(ff), Store.NO));
						doc.add(new LongField("size", FileUtils.sizeOf(ff), Store.YES));
						indexWriter.addDocument(doc);
						looger.info("创建[" + ff.getName() + "]索引完成,耗时[" + (new Date().getTime() - st.getTime()) + "]ms");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				indexWriter.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void getDirectoryIndexInfo() {
		try {
			directoryReader = DirectoryReader.open(directory);

			int maxDoc = directoryReader.maxDoc();
			int numDocs = directoryReader.numDocs();
			int numDeletedDocs = directoryReader.numDeletedDocs();
			looger.info("存储的文档数numDocs:" + numDocs);
			looger.info("存储的总文档数maxDoc：" + maxDoc);
			looger.info("删除的总文档数maxDoc：" + numDeletedDocs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void queryIndex(String seach) {
		try {
			directoryReader = DirectoryReader.open(directory);
			DirectoryReader ifChanged = DirectoryReader.openIfChanged(directoryReader);
			if (null != ifChanged) {
				directoryReader.close();
				directoryReader = ifChanged;
			}
			IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

			QueryParser queryParser = new QueryParser("context", analyzer);
			Query query = queryParser.parse(seach);

			// Query query = new TermQuery(new Term("name", seach));
			TopDocs topDocs = indexSearcher.search(query, 100);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				looger.info("查找到：" + indexSearcher.doc(sd.doc).get("name"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteAllIndex() {
		try {
			indexWriter.deleteAll();
			looger.info("删除全部索引");
			// indexWriter.rollback();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				indexWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void unDelete() {
		try {
			indexWriter.rollback();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateIndex(Term term, Document doc) {
		try {
			indexWriter.updateDocument(term, doc);
			looger.info("更新");
			indexWriter.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void forceMergeDeletes() {
		try {
			indexWriter.forceMergeDeletes();
			indexWriter.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				directory.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void createBootIndex(String path) {
		File f = new File(path);
		if (f.isDirectory()) {
			for (File ff : f.listFiles()) {
				if (ff.getName().endsWith(".txt") || ff.getName().endsWith(".java") || ff.getName().endsWith(".sql")) {
					Date st = new Date();
					looger.info("开始创建[" + ff.getName() + "]索引.....");
					try {
						Document doc = new Document();
						String name = ff.getName();
						TextField textField = new TextField("context", FileUtils.readFileToString(ff), Store.NO);
						String extension = FilenameUtils.getExtension(name);
						Float float1 = bootMap.get(extension);
						if (null != float1) {
							looger.info("为" + name + "进行加权操作，加权值为：" + float1);
							textField.setBoost(float1);
						}
						doc.add(new StringField("name", ff.getName(), Store.YES));
						doc.add(textField);
						doc.add(new LongField("size", FileUtils.sizeOf(ff), Store.YES));
						indexWriter.addDocument(doc);
						looger.info("创建[" + ff.getName() + "]索引完成,耗时[" + (new Date().getTime() - st.getTime()) + "]ms");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				indexWriter.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

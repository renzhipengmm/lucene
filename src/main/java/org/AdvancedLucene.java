package org;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.commons.io.FileUtils;
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
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NRTCachingDirectory;

public class AdvancedLucene {
	private Directory directory;
	private Directory cacheFsDir;
	private IndexWriter writer;
	private TrackingIndexWriter trackingIndexWriter;
	private Analyzer analyzer;

	private static SearcherManager searcherManager;
	private ControlledRealTimeReopenThread<IndexSearcher> crtThread;

	Logger logger = Logger.getLogger(this.getClass());

	public AdvancedLucene() {
		try {
			directory = FSDirectory.open(Paths.get("E:" + File.separator + "lucene"));
			cacheFsDir = new NRTCachingDirectory(directory, 5d, 60d);
			analyzer = new StandardAnalyzer();
			writer = new IndexWriter(cacheFsDir, new IndexWriterConfig(analyzer));
			trackingIndexWriter = new TrackingIndexWriter(writer);
			searcherManager = new SearcherManager(writer, true, new SearcherFactory());
			crtThread = new ControlledRealTimeReopenThread<IndexSearcher>(trackingIndexWriter, searcherManager, 5.0, 0.025);
			crtThread.setDaemon(true);
			crtThread.setName("ControlledRealTimeReopenThread");
			crtThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void createIndex(String path) {
		File f = new File(path);
		if (f.isDirectory()) {
			for (File ff : f.listFiles()) {
				if (ff.getName().endsWith(".txt") || ff.getName().endsWith(".java") || ff.getName().endsWith(".sql")) {
					Date st = new Date();
					logger.info("开始创建[" + ff.getName() + "]索引.....");
					try {
						// 这是一条索引
						Document doc = new Document();
						// 为索引添加 字段
						doc.add(new StringField("name", ff.getName(), Store.YES));
						doc.add(new TextField("context", FileUtils.readFileToString(ff), Store.NO));
						doc.add(new LongField("size", FileUtils.sizeOf(ff), Store.YES));
						// 用writer写入索引
						trackingIndexWriter.addDocument(doc);
						logger.info("创建[" + ff.getName() + "]索引完成,耗时[" + (new Date().getTime() - st.getTime()) + "]ms");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		try {
			trackingIndexWriter.getIndexWriter().commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getDirectoryIndexInfo() {
		try {
			DirectoryReader directoryReader = DirectoryReader.open(cacheFsDir);
			int maxDoc = directoryReader.maxDoc();
			int numDocs = directoryReader.numDocs();
			int numDeletedDocs = directoryReader.numDeletedDocs();
			logger.info("存储的文档数numDocs:" + numDocs);
			logger.info("存储的总文档数maxDoc：" + maxDoc);
			logger.info("删除的总文档数maxDoc：" + numDeletedDocs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void quertyByTermSearchManage(String fileName) {
		IndexSearcher searcher = null;
		try {
			searcher = searcherManager.acquire();
			TopDocs search = searcher.search(new TermQuery(new Term("name", fileName)), 100);
			showSearchInfo(search, searcher);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				searcherManager.release(searcher);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void queryBySearchManage(String query, int num) {
		IndexSearcher searcher = null;
		try {
			searcher = searcherManager.acquire();
			QueryParser queyParser = new QueryParser("context", analyzer);
			Query q = queyParser.parse(query);
			TopDocs search = searcher.search(q, num);
			showSearchInfo(search, searcher);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			try {
				searcherManager.release(searcher);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void commitIndexChage() {
		crtThread.interrupt();
		crtThread.close();
		try {
			writer.commit();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void showSearchInfo(TopDocs search, IndexSearcher searcher) {
		try {
			logger.info("一共检索到匹配的文档数:[" + search.totalHits + "]");
			ScoreDoc[] scoreDocs = search.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				logger.info("匹配到的文档[" + sd.doc + "]-[" + doc.get("name") + "]");
			}
		} catch (IOException e) {
		}
	}

	public void deleteIndex(String fileName) {
		try {
			trackingIndexWriter.deleteDocuments(new Term("name", fileName));
			logger.info("删除文档:[" + fileName + "]");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteAllIndex() {
		try {
			trackingIndexWriter.deleteAll();
			logger.info("删除全部索引");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				trackingIndexWriter.getIndexWriter().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void unDelete() {
		// trackingIndexWriter.getIndexWriter().
	}

	public void forceMergeDeletes() {
		try {
			trackingIndexWriter.getIndexWriter().forceMergeDeletes();
			trackingIndexWriter.getIndexWriter().commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

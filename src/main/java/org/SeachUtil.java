package org;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SeachUtil {
	private static Directory directory;
	private static Analyzer analyzer;
	private static DirectoryReader reader = null;

	private static SearcherManager searcherManager;

	Logger looger = Logger.getLogger(this.getClass());
	private Query parse;
	private TopDocs search;

	public SeachUtil() {
		Date st = new Date();
		looger.info("初始化索引库....");
		try {
			directory = FSDirectory.open(Paths.get("E:" + File.separator + "lucene"));
			analyzer = new StandardAnalyzer();
			if (reader == null) {
				reader = DirectoryReader.open(directory);
			} else {
				DirectoryReader new_ = DirectoryReader.openIfChanged(reader);
				if (null != new_) {
					reader.close();
					reader = new_;
				}
			}
			searcherManager = new SearcherManager(directory, new SearcherFactory());
			looger.info("初始化索引库完成,耗时[" + (new Date().getTime() - st.getTime()) + "]ms");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void seachByTerm(String field, String value, int num) {
		IndexSearcher indexSearch = new IndexSearcher(reader);
		Query query = new TermQuery(new Term(field, value));
		try {
			TopDocs search = indexSearch.search(query, num);
			int totalHits = search.totalHits;
			looger.info("一个匹配到了" + totalHits + "篇文档");
			ScoreDoc[] scoreDocs = search.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = indexSearch.doc(sd.doc);
				looger.info("查询到文档[" + doc.get("name") + "]");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void seachByTermRange(String field, String start, String end, int num) {
		IndexSearcher indexSearch = new IndexSearcher(reader);
		Query query = TermRangeQuery.newStringRange(field, start, end, true, true);
		try {
			TopDocs search = indexSearch.search(query, num);
			int totalHits = search.totalHits;
			looger.info("一个匹配到了" + totalHits + "篇文档");
			ScoreDoc[] scoreDocs = search.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = indexSearch.doc(sd.doc);
				looger.info("查询到文档" + doc.get("name"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void seachByNumLongTermRanage(String field, long start, long end, int num) {
		IndexSearcher indexSearch = new IndexSearcher(reader);
		Query query = NumericRangeQuery.newLongRange(field, start, end, true, true);
		try {
			TopDocs search = indexSearch.search(query, num);
			int totalHits = search.totalHits;
			looger.info("一个匹配到了" + totalHits + "篇文档");
			ScoreDoc[] scoreDocs = search.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = indexSearch.doc(sd.doc);
				looger.info("查询到文档" + doc.get("name"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void seachByPrefixQuery(String field, String value, int num) {
		IndexSearcher indexSearch = new IndexSearcher(reader);
		Query query = new PrefixQuery(new Term(field, value));
		try {
			TopDocs search = indexSearch.search(query, num);
			int totalHits = search.totalHits;
			looger.info("一个匹配到了" + totalHits + "篇文档");
			ScoreDoc[] scoreDocs = search.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = indexSearch.doc(sd.doc);
				looger.info("查询到文档" + doc.get("name"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void seachByWildcardQuery(String field, String wild, int num) {
		IndexSearcher indexSearch = new IndexSearcher(reader);
		Query query = new WildcardQuery(new Term(field, wild));
		try {
			TopDocs search = indexSearch.search(query, num);
			int totalHits = search.totalHits;
			looger.info("一个匹配到了" + totalHits + "篇文档");
			ScoreDoc[] scoreDocs = search.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = indexSearch.doc(sd.doc);
				looger.info("查询到文档" + doc.get("name"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void seachByBooleanQuery(BooleanQuery booleanQuery, int num) {
		IndexSearcher indexSearch = new IndexSearcher(reader);
		try {
			TopDocs search = indexSearch.search(booleanQuery, num);
			int totalHits = search.totalHits;
			looger.info("一个匹配到了" + totalHits + "篇文档");
			ScoreDoc[] scoreDocs = search.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = indexSearch.doc(sd.doc);
				looger.info("查询到文档" + doc.get("name"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void seachByPhraseQuery(PhraseQuery phraseQuery, int num) {
		IndexSearcher indexSearch = new IndexSearcher(reader);
		try {
			TopDocs search = indexSearch.search(phraseQuery, num);
			int totalHits = search.totalHits;
			looger.info("一个匹配到了" + totalHits + "篇文档");
			ScoreDoc[] scoreDocs = search.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = indexSearch.doc(sd.doc);
				looger.info("查询到文档" + doc.get("name"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void seachByFuzzyQuery(FuzzyQuery fuzzyQuery, int num) {
		IndexSearcher indexSearch = new IndexSearcher(reader);
		try {
			TopDocs search = indexSearch.search(fuzzyQuery, num);
			int totalHits = search.totalHits;
			looger.info("一个匹配到了" + totalHits + "篇文档");
			ScoreDoc[] scoreDocs = search.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = indexSearch.doc(sd.doc);
				looger.info("查询到文档" + doc.get("name"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void seachByQueryParse(Query query, int num) {
		IndexSearcher indexSearch = new IndexSearcher(reader);
		try {
			TopDocs search = indexSearch.search(query, num);
			int totalHits = search.totalHits;
			looger.info("一个匹配到了" + totalHits + "篇文档");
			ScoreDoc[] scoreDocs = search.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = indexSearch.doc(sd.doc);
				looger.info("查询到文档[" + doc.get("name") + "]");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void seachBySearchManager(Query query, int num) {
		IndexSearcher indexSearch = null;
		try {
			indexSearch = searcherManager.acquire();
			TopDocs search = indexSearch.search(query, num);
			int totalHits = search.totalHits;
			looger.info("一个匹配到了" + totalHits + "篇文档");
			ScoreDoc[] scoreDocs = search.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = indexSearch.doc(sd.doc);
				looger.info("查询到文档[" + doc.get("name") + "]");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				searcherManager.release(indexSearch);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void seachePage(String query, int pageIndex, int pageSize) {
		try {
			IndexSearcher indexSearch = new IndexSearcher(reader);
			QueryParser parser = new QueryParser("context", analyzer);
			Query q = parser.parse(query);
			int prePageLastIndex = (pageIndex - 1) * pageSize;
			if (prePageLastIndex == 0) {
				prePageLastIndex = pageSize;
			}
			TopDocs topDocs = indexSearch.search(q, prePageLastIndex);
			ScoreDoc scoreDoc = topDocs.scoreDocs[prePageLastIndex - 1];
			if (pageIndex == 1) {
				scoreDoc = null;
			}
			TopDocs searchAfter = indexSearch.searchAfter(scoreDoc, q, pageSize);
			looger.info("一个匹配到了" + searchAfter.totalHits + "篇文档");
			ScoreDoc[] scoreDocs = searchAfter.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = indexSearch.doc(sd.doc);
				looger.info("查询到文档[" + doc.get("name") + "]");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package org;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) throws IOException {
		Directory directory = FSDirectory.open(Paths.get("E:\\新基础平台升级文档"));
		// Directory directory = new RAMDirectory();
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);
		File f = new File("E:" + File.separator + "my");
		for (File ff : f.listFiles()) {
			if (ff.isFile()) {
				if (ff.getName().endsWith(".txt") || ff.getName().endsWith(".java") || ff.getName().endsWith(".sql")) {
					System.out.println("开始写入文件:[" + ff.getName() + "]");
					Date sd = new Date();
					Document doc = new Document();
					doc.add(new Field("name", ff.getName(), TextField.TYPE_STORED));

					doc.add(new Field("path", ff.getPath(), TextField.TYPE_STORED));
					doc.add(new Field("context", new BufferedReader(new InputStreamReader(new FileInputStream(ff))), TextField.TYPE_NOT_STORED));
					StringField field = new StringField("X", "X", Store.YES);
					field.setBoost(1f);
					iwriter.addDocument(doc);
					Date nd = new Date();
					System.out.println("写入索引:[" + ff.getName() + "]完毕,共耗时[" + (nd.getTime() - sd.getTime()) + "]ms");
					System.out.println("-----------------");
				}
			}
		}
		iwriter.close();
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		QueryParser parser = new QueryParser("context", analyzer);
		try {
			Query query = parser.parse("创建");
			TopDocs search = isearcher.search(query, 1000);
			ScoreDoc[] scoreDocs = search.scoreDocs;
			System.out.println(scoreDocs.length);
			for (ScoreDoc sd : scoreDocs) {
				Document doc = isearcher.doc(sd.doc);
				System.out.println(isearcher.doc(sd.doc).get("name"));
			}

			ireader.close();
			directory.close();

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
}

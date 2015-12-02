package org;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;

public class TestSearchIndex {
	@Test
	public void testSearchTerm() {
		SeachUtil seach = new SeachUtil();
		seach.seachByTerm("context", "select", 100);
	}

	@Test
	public void testSearchTermRanageQuery() {
		SeachUtil seach = new SeachUtil();
		seach.seachByTermRange("name", "select.java", "select.sql", 1);
	}

	@Test
	public void testSearchLongTermRanageQuery() {
		SeachUtil seach = new SeachUtil();
		seach.seachByNumLongTermRanage("size", 1l, 5000l, 8);
	}

	@Test
	public void testSearchByPrefixQuery() {
		SeachUtil seach = new SeachUtil();
		seach.seachByPrefixQuery("context", "sele", 2);
	}

	@Test
	public void testSearchByWildCardQuery() {
		SeachUtil seach = new SeachUtil();
		seach.seachByWildcardQuery("context", "publi?", 10);
	}

	@Test
	public void testSearchByBooleanQuery() {
		SeachUtil seach = new SeachUtil();
		Builder builder = new BooleanQuery.Builder();
		builder.add(new TermQuery(new Term("context", "select")), Occur.MUST);
		builder.add(new TermQuery(new Term("context", "like")), Occur.MUST);
		BooleanQuery booleanQuery = builder.build();
		seach.seachByBooleanQuery(booleanQuery, 10);
	}

	@Test
	public void testSearchByPhraseQuery() {
		SeachUtil seach = new SeachUtil();
		PhraseQuery query = new PhraseQuery(1, "context", "public", "double");
		seach.seachByPhraseQuery(query, 10);
	}

	@Test
	public void testSearchByFuzzyQuery() {
		SeachUtil seach = new SeachUtil();
		FuzzyQuery query = new FuzzyQuery(new Term("context", "sele"));
		seach.seachByFuzzyQuery(query, 10);
	}

	@Test
	public void testSearchByQueryParse() {
		QueryParser queryParser = new QueryParser("context", new StandardAnalyzer());
		try {
			Query query = queryParser.parse("select");
			// 包含以下内容 为OR 的 或 关系 默认空格为OR
			query = queryParser.parse("select  from");
			// 可以更改默认为AND 关系
			// queryParser.setDefaultOperator(Operator.AND);
			// 包含以下内容 为 AND 的 并且 关系
			query = queryParser.parse("select AND from AND appr");
			// 改变搜索域的位置
			query = queryParser.parse("name:select.sql");
			// 使用通配符进行 查询 ? *
			query = queryParser.parse("selec?");
			// + 表示域中必须有 -必须不含有
			query = queryParser.parse(" + name:教师*");
			// 闭区间查询
			query = queryParser.parse("[public  select]");
			// 开区间查询
			query = queryParser.parse("{public  select}");
			// 完全匹配字符串
			query = queryParser.parse("\"stu.historyclassinfo like\"");
			// 匹配 字之间 有一个 距离
			query = queryParser.parse("\"LEFT  BASE_USERINFO\"~1");
			// 模糊查询
			query = queryParser.parse("BASE_USERIxX~");

			SeachUtil seach = new SeachUtil();
			seach.seachByQueryParse(query, 100);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testQueryPage() {
		SeachUtil seach = new SeachUtil();
		seach.seachePage("select", 1, 2);
		System.out.println("-------------");
		seach.seachePage("select", 2, 2);
		System.out.println("-------------");
		seach.seachePage("select", 3, 2);
	}
	
	
}

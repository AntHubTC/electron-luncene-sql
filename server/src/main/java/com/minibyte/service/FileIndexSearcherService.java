package com.minibyte.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 文件索引检索服务
 *
 * @author:
 * @date:
 * @description:
 */
@Service
public class FileIndexSearcherService {
    public void run() throws Exception {
        // 设置索引存储路径
        Directory indexDir = FSDirectory.open(new File("index-dir").toPath());

        // 创建索引读取器
        IndexReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        QueryParser queryParser = new QueryParser("content", new StandardAnalyzer());
        Query query = queryParser.parse("hello");
        TopDocs topDocs = indexSearcher.search(query, 100);
        for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = indexReader.document(scoreDoc.doc);
            System.out.println("File: "
                    + doc.get("content"));
        }

        // 关闭索引读取器
        indexReader.close();
    }

    public static void main(String[] args) {
        try {
            FileIndexSearcherService fileIndexSearcherService = new FileIndexSearcherService();
            fileIndexSearcherService.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

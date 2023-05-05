package com.minibyte.test.luncene;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author tangcheng_cd
 * @version 1.0
 * @className LunceneTest
 * @description
 * @date 2023/4/20 16:49
 **/
public class LunceneTest {
    public static void main(String[] args) {
        try {
            new LunceneTest().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run () throws Exception{
        indexWrite();
        indexReader();
    }

    private void indexReader() throws ParseException, IOException {
        // 索引目录对象
        Directory directory = FSDirectory.open(new File("index-dir").toPath());
        // 索引读取工具
        IndexReader reader = DirectoryReader.open(directory);
        // 索引搜索工具
        IndexSearcher searcher = new IndexSearcher(reader);

        // 创建查询解析器,两个参数：默认要查询的字段的名称，分词器
        QueryParser parser = new QueryParser("title", new SmartChineseAnalyzer());
        // 创建查询对象
        Query query = parser.parse("谷歌网站的信息");

        // 搜索数据,两个参数：查询条件对象要查询的最大结果条数
        // 返回的结果是 按照匹配度排名得分前N名的文档信息（包含查询到的总条数信息、所有符合条件的文档的编号信息）。
        TopDocs topDocs = searcher.search(query, 10);
        // 获取总条数
        System.out.println("本次搜索共找到" + topDocs.totalHits + "条数据");
        // 获取得分文档对象（ScoreDoc）数组.SocreDoc中包含：文档的编号、文档的得分
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            // 取出文档编号
            int docID = scoreDoc.doc;
            // 根据编号去找文档
            Document doc = reader.document(docID);
            System.out.println("id: " + doc.get("id"));
            System.out.println("title: " + doc.get("title"));
            // 取出文档得分
            System.out.println("得分： " + scoreDoc.score);
        }
        directory.close();
    }

    private void indexWrite() throws IOException {
        // 设置索引存储路径
        Directory indexDir = FSDirectory.open(new File("index-dir").toPath());

        // 创建索引配置对象，使用标准分词器
        // IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriterConfig config = new IndexWriterConfig(new SmartChineseAnalyzer());
        // 设置是否清空索引库中的数据
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        // 创建索引写入器
        IndexWriter indexWriter = new IndexWriter(indexDir, config);

        // 读取本地文件，并将内容创建成Document对象
        Collection<Document> docs = new ArrayList<>();

        // 创建文档对象
        Document document1 = new Document();
        document1.add(new StringField("id", "1", Field.Store.YES));
        document1.add(new TextField("title", "谷歌地图之父跳槽facebook", Field.Store.YES));
        docs.add(document1);
        // 创建文档对象
        Document document2 = new Document();
        document2.add(new StringField("id", "2", Field.Store.YES));
        document2.add(new TextField("title", "谷歌地图之父加盟FaceBook", Field.Store.YES));
        docs.add(document2);
        // 创建文档对象
        Document document3 = new Document();
        document3.add(new StringField("id", "3", Field.Store.YES));
        document3.add(new TextField("title", "谷歌地图创始人拉斯离开谷歌加盟Facebook", Field.Store.YES));
        docs.add(document3);
        // 创建文档对象
        Document document4 = new Document();
        document4.add(new StringField("id", "4", Field.Store.YES));
        document4.add(new TextField("title", "谷歌地图之父跳槽Facebook与Wave项目取消有关", Field.Store.YES));
        docs.add(document4);
        // 创建文档对象
        Document document5 = new Document();
        document5.add(new StringField("id", "5", Field.Store.YES));
        document5.add(new TextField("title", "谷歌地图之父拉斯加盟社交网站Facebook", Field.Store.YES));
        docs.add(document5);

        // 将Document对象添加到索引中
        indexWriter.addDocuments(docs);

        // 提交
        indexWriter.commit();
        // 关闭索引写入器
        indexWriter.close();
    }
}

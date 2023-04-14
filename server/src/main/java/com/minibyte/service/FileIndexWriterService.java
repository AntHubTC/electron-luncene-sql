package com.minibyte.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;

/**
 * 文件索引服务
 *
 * @author: tangcheng_cd
 * @date: 2023/4/14
 * @description:
 */
@Service
public class FileIndexWriterService {
    public void run () throws Exception{
        // 设置索引存储路径
        Directory indexDir = FSDirectory.open(new File("index-dir").toPath());

        // 创建索引配置对象，使用标准分词器
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());

        // 创建索引写入器
        IndexWriter indexWriter = new IndexWriter(indexDir, config);

        // 读取本地文件，并将内容创建成Document对象
        Document doc = new Document();
        File file = new File("sample-file.txt");
        FieldType filedType = new FieldType();
        filedType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        filedType.setStored(true);
        filedType.setTokenized(true);
        Field content = new Field("content", new FileReader(file), filedType);

        doc.add(content);

        // 将Document对象添加到索引中
        indexWriter.addDocument(doc);

        // 关闭索引写入器
        indexWriter.close();
    }
}

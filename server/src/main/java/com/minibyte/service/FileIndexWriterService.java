package com.minibyte.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import com.minibyte.bo.pojo.app.UpdateDocDto;
import com.minibyte.common.enums.SQL_FILE_IDX_FILED;
import com.minibyte.common.exception.MBBizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 文件索引服务
 *
 *  https://blog.csdn.net/weixin_42633131/article/details/82873731/
 *
 * @author: tangcheng_cd
 * @date: 2023/4/14
 * @description:
 */
@Slf4j
@Service
public class FileIndexWriterService {
    public static final String INDEX_DIR = "index-dir";

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        new FileIndexWriterService().run();
//        new FileIndexWriterService().list();
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }

    public void list() throws Exception {
        // 索引目录对象
        Directory directory = FSDirectory.open(new File("index-dir").toPath());
        // 索引读取工具
        IndexReader reader = DirectoryReader.open(directory);
        for (int i = 0; i < reader.maxDoc(); i++) {
            Document document = reader.document(i);
            System.out.println(" fileName:" + document.get(SQL_FILE_IDX_FILED.fileName));
            System.out.println(" filePath:" + document.get(SQL_FILE_IDX_FILED.filePath));
            System.out.println(" name:" + document.get(SQL_FILE_IDX_FILED.sqlName));
            System.out.println(" detail:" + document.get(SQL_FILE_IDX_FILED.detail));
            System.out.println(" content:" + document.get(SQL_FILE_IDX_FILED.content));
            System.out.println();
        }
    }

    public void run () throws Exception{
        List<File> files = FileUtil.loopFiles("D:\\xinchao\\sql_index\\SSP常用");
        if (CollUtil.isEmpty(files)) {
            return;
        }

        File indexDirFile = new File(INDEX_DIR);
        ensureExistIndexLab(indexDirFile);

        // 设置索引存储路径
        try (Directory indexDir = FSDirectory.open(indexDirFile.toPath())) {
            // 创建索引配置对象，使用标准分词器
            // IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            IndexWriterConfig config = new IndexWriterConfig(new SmartChineseAnalyzer());
            // 设置是否清空索引库中的数据
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            // 创建索引写入器
            IndexWriter indexWriter = new IndexWriter(indexDir, config);

            // 创建索引读取器
            IndexReader indexReader = DirectoryReader.open(indexDir);

            // 读取本地文件，并将内容创建成Document对象
            Collection<Document> addDocs = new ArrayList<>();
            Collection<UpdateDocDto> updateDocs = new ArrayList<>();

            for (File file : files) {
                if (checkFile(file)) {
                    collectFileDocs(file, indexReader, addDocs, updateDocs);
                }
            }

            // 将Document对象添加到索引中
            if (CollUtil.isNotEmpty(addDocs)) {
                indexWriter.addDocuments(addDocs);
            }
            for (UpdateDocDto updateDoc : updateDocs) {
                indexWriter.updateDocument(updateDoc.getTerm(), updateDoc.getDocument());
            }

            if (CollUtil.isNotEmpty(addDocs) || CollUtil.isNotEmpty(updateDocs)) {
                // 提交
                indexWriter.commit();
            }
            // 关闭索引写入器
            indexWriter.close();
            // 关闭索引读取器
            indexReader.close();
        }
    }

    /**
     * 确保索引库存在
     * @param indexDirFile
     * @throws Exception
     */
    private void ensureExistIndexLab(File indexDirFile) throws Exception {
        if (ArrayUtil.isNotEmpty(indexDirFile.list())) {
            return;
        }
        try (Directory indexDir = FSDirectory.open(indexDirFile.toPath())) {
            IndexWriterConfig config = new IndexWriterConfig(new SmartChineseAnalyzer());
            // 设置是否清空索引库中的数据
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            // 创建索引写入器
            IndexWriter indexWriter = new IndexWriter(indexDir, config);
            // 关闭索引写入器
            indexWriter.close();
        }
    }

    private boolean checkFile(File file) {
        if (file.length() > 8 * 1024 * 1024 * 200) {
            log.warn("文件超过200M，不能进行索引! 文件：{}", file.toPath());
            return false;
        }
        return true;
    }

    private static final String NAME_PREFIX = "-- @name";
    private static final String DETAIL_PREFIX = "-- @detail";
    private void collectFileDocs(File file, IndexReader indexReader, Collection<Document> addDocs, Collection<UpdateDocDto> updateDocs) throws Exception {
        log.info("正在索引文件:{}", file.toString());
        List<String> lines = FileUtil.readLines(file, StandardCharsets.UTF_8);
        Map<String, String> metaInfo = readContentMetaInfo(lines);
        String content = CollUtil.join(lines, "\r\n");

        String fileSizeHash = getFileHash(Collections.singletonList(file.getAbsolutePath() + file.length()));
        log.debug("fileHash:{}", fileSizeHash);
        String fileContentHash = getFileHash(lines);
        log.debug("fileHash2:{}", fileContentHash);

        // 创建文档对象
        Document document1 = new Document();
        document1.add(new TextField(SQL_FILE_IDX_FILED.fileName, file.getName(), Field.Store.YES));
        document1.add(new StringField(SQL_FILE_IDX_FILED.filePath, file.getPath(), Field.Store.YES));
        document1.add(new StringField(SQL_FILE_IDX_FILED.fileSizeHash, fileSizeHash, Field.Store.YES));
        document1.add(new StringField(SQL_FILE_IDX_FILED.fileContentHash, fileContentHash, Field.Store.YES));
        document1.add(new StringField(SQL_FILE_IDX_FILED.sqlName, metaInfo.get(NAME_PREFIX), Field.Store.YES));
        document1.add(new StringField(SQL_FILE_IDX_FILED.detail, metaInfo.getOrDefault(DETAIL_PREFIX, metaInfo.get(NAME_PREFIX)), Field.Store.YES));
        document1.add(new TextField(SQL_FILE_IDX_FILED.content, content, Field.Store.YES));

        // 通过文件路径搜索，查看索引中是否存在了这个文件；
        // https://blog.csdn.net/yelllowcong/article/details/78698506
//        QueryParser queryParser = new QueryParser(SQL_FILE_IDX_FILED.filePath, new SimpleAnalyzer());
//        Query query = queryParser.parse(file.getPath());
//        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Query query = new TermQuery(new Term(SQL_FILE_IDX_FILED.filePath, file.getPath()));
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        TopDocs topDocs = indexSearcher.search(query, 1);
        boolean existFile = topDocs.totalHits.value > 0;

        if (existFile) {
            int docId = topDocs.scoreDocs[0].doc;
            Document doc = indexReader.document(docId);
            String oldFileSizeHash = doc.get(SQL_FILE_IDX_FILED.fileSizeHash);
            String oldFileContentHash = doc.get(SQL_FILE_IDX_FILED.fileContentHash);
            // 只要有一个不匹配就要进行更新
            if (!fileSizeHash.equals(oldFileSizeHash) || !fileContentHash.equals(oldFileContentHash)) {
                updateDocs.add(new UpdateDocDto(new Term(SQL_FILE_IDX_FILED.filePath, file.getPath()), document1));
            }
        } else {
            addDocs.add(document1);
        }
    }

    private String getFileHash(List<String> lines) {
        StringBuilder hashBuilder = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            for (String line : lines) {
                md.update(line.getBytes(StandardCharsets.UTF_8));
            }
            for (byte b : md.digest()) {
                hashBuilder.append(String.format("%02x", b));
            }
        } catch (NoSuchAlgorithmException e) {
            log.error("系统异常", e);
            throw new MBBizException("无此算法类型");
        }
        return hashBuilder.toString();
    }

    public String getFileHash(File file) {
        StringBuilder hashBuilder = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            try (FileInputStream fis = new FileInputStream(file)) {
                FileChannel ch = fis.getChannel();
                MappedByteBuffer buffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
                md.update(buffer);
            }
            for (byte b : md.digest()) {
                hashBuilder.append(String.format("%02x", b));
            }
        } catch (NoSuchAlgorithmException e) {
            log.error("系统异常", e);
            throw new MBBizException("无此算法类型");
        } catch (IOException e) {
            log.error("系统异常", e);
            throw new MBBizException("IO Exception");
        }
        return hashBuilder.toString();
    }

    private Map<String, String> readContentMetaInfo(List<String> lines) {
        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder detailBuilder = new StringBuilder();

        if (CollUtil.isNotEmpty(lines)) {
            for (String line : lines) {
                if (!line.startsWith("--")) {
                    break;
                }
                if (line.startsWith(NAME_PREFIX)) {
                    nameBuilder.append(line.substring(NAME_PREFIX.length())).append(";");
                }
                if (line.startsWith(DETAIL_PREFIX)) {
                    detailBuilder.append(line.substring(DETAIL_PREFIX.length())).append(";");
                }
            }
            if (nameBuilder.length() == 0) {
                String firstLine = lines.get(0);
                if (firstLine.startsWith("--")) {
                    firstLine = firstLine.substring("--".length());
                }
                nameBuilder.append(firstLine);
            }
        }

        Map<String, String> resMap = new HashMap<>();
        resMap.put(NAME_PREFIX, nameBuilder.toString());
        resMap.put(DETAIL_PREFIX, detailBuilder.toString());
        return resMap;
    }
}

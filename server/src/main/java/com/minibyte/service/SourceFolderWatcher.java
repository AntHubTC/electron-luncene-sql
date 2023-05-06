package com.minibyte.service;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * @author tangcheng_cd
 * @version 1.0
 * @className SourceFolderWatcher
 * @description
 * @date 2023/5/6 12:49
 **/
public class SourceFolderWatcher {
    public static void main(String[] args) throws Exception {
        // 创建WatchService对象，监控目标目录
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get("D:\\xinchao\\sql_index\\SSP常用");
        dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

        // 开始监听文件变化
        while (true) {
            WatchKey key = watchService.take();// tabke方法会阻塞到有变化的时候继续执行
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY || kind == ENTRY_DELETE) {
                    String fileName = event.context().toString();
                    System.out.println(kind + " event on " + fileName);
                    // TODO:tc: 用此机制来检测文件发生修改，然后进行实时重新索引
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }
}

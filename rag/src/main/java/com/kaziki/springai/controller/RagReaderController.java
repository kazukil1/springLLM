package com.kaziki.springai.controller;

import com.kaziki.springai.cleaner.DocumentCleaner;
import com.kaziki.springai.reader.DocumentReaderFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rag")
public class RagReaderController {
    @Autowired
    private DocumentReaderFactory documentReaderFactory;
    @GetMapping("/read")
    public List<Document> readDocument(@RequestParam("path") String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("文件不存在或不是有效文件: " + path);
        }
        try {

            List<Document> documents = DocumentCleaner.cleanDocuments(documentReaderFactory.read(file));

            return DocumentCleaner.cleanDocuments(documents);

        } catch (IOException e) {
            throw new RuntimeException("读取文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 文本清洗
     */

}

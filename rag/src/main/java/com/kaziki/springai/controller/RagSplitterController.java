package com.kaziki.springai.controller;

import com.kaziki.springai.cleaner.DocumentCleaner;
import com.kaziki.springai.reader.DocumentReaderFactory;
import com.kaziki.springai.spiltter.OverlapParagraphTextSplitter;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/rag")
public class RagSplitterController {
    @Autowired
    private DocumentReaderFactory documentReaderFactory;

    @GetMapping("/split")
    public String split(@RequestParam("path") String path) {
        List<Document> documents;
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("文件不存在或不是有效文件: " + path);
        }
        try {

            documents = DocumentCleaner.cleanDocuments(documentReaderFactory.read(file));
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败: " + e.getMessage(), e);
        }
        for (Document document : documents){
            System.out.println("before chuck:"+document.getText());
            System.out.println("");
            OverlapParagraphTextSplitter tokenTextSplitter=new OverlapParagraphTextSplitter(
                    //每块最多600个token，每块之间最多300个token，最多5块，总长度不超过8000个token，暴留句号,换行符
                    100,5
            );
            List<Document> chuckedDocuments=tokenTextSplitter.split(document);
            for (Document chuckedDocument : chuckedDocuments){
                System.out.println("after chuck:"+chuckedDocument.getText());
                System.out.println("");
            }
            System.out.println("======");
        }
        return "success";
    }

}

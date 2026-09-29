package com.kaziki.springai.controller;

import com.kaziki.springai.reader.DocumentReaderFactory;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/rag")
public class RagReaderController {
    @Autowired
    private DocumentReaderFactory documentReaderFactory;

    @GetMapping("/read")
    public String read(String filePath) throws IOException {
        File file = new File(filePath);
        List<Document> documents =
                documentReaderFactory.read(file);
        for (Document document : documents) {
            System.out.println(document.getText());
            System.out.println(document.getMetadata());
            System.out.println("");
        }
        return "Read successfully";
    }
}

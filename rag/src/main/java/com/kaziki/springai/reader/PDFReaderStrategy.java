package com.kaziki.springai.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
@Service
public class PDFReaderStrategy implements DocumentReaderStrategy {
    @Override
    public boolean supports(File file) {
        String name =file.getName().toLowerCase();

        return name.endsWith(".pdf");
    }

    @Override
    public List<Document> read(File file) throws IOException {
        Resource resource =new FileSystemResource(file);
        PagePdfDocumentReader pagePdfDocumentReader=new PagePdfDocumentReader(resource);

        //读取配置
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPageTopMargin(50)  //忽略页面顶部的50行
                .withPageBottomMargin(50)   //忽略页面底部的50行
                .withPagesPerDocument(1)//每页一个文档
                .withPageExtractedTextFormatter(
                        new ExtractedTextFormatter.Builder()
                                .withNumberOfTopTextLinesToDelete(0)//每页 删除0行
                                .build()
                ).build();
        return pagePdfDocumentReader.get();
    }
}

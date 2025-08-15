package com.assistant.personalsystem.controller;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Phrase;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/convert")
public class DocumentConverterController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentConverterController.class);

    @PostMapping("/word-to-pdf")
    public ResponseEntity<Resource> convertWordToPdf(@RequestParam("file") MultipartFile file) {
        try {
            String text = "";
            String filename = file.getOriginalFilename();
            logger.info("Converting file: {}", filename);

            if (filename != null && filename.endsWith(".docx")) {
                try (InputStream is = file.getInputStream()) {
                    // 处理 .docx 文件
                    XWPFDocument wordDocument = new XWPFDocument(is);
                    XWPFWordExtractor extractor = new XWPFWordExtractor(wordDocument);
                    text = extractor.getText();
                    logger.info("Extracted text from Word document: {}", text);
                    extractor.close();
                    wordDocument.close();
                }
            } else {
                // 如果文件不是 .docx Word 文档
                 logger.warn("Unsupported file format: {}", filename);
                 return ResponseEntity.badRequest().body(new ByteArrayResource("Unsupported file format. Please upload a .docx file.".getBytes()));
            }

            // 创建PDF文档
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            Document pdfDocument = new Document();

            try {
                PdfWriter.getInstance(pdfDocument, pdfOutputStream);
                pdfDocument.open();
                logger.info("PDF document opened successfully.");
            } catch (ExceptionConverter e) {
                logger.error("Error initializing PDF writer: ", e);
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }

            // 设置中文字体
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font chineseFont = new Font(baseFont, 12, Font.NORMAL);

            // 将提取的纯文本内容添加到PDF
            if (text != null && !text.isEmpty()) {
                pdfDocument.add(new Phrase(new Chunk(text, chineseFont)));
                logger.info("Text added to PDF document using Chunk and Phrase with Chinese font.");
            } else {
                // 如果提取的文本为空，添加一个提示
                pdfDocument.add(new Phrase(new Chunk("The Word document was empty or contained no readable text.", chineseFont)));
                logger.warn("No text was extracted from the Word document.");
            }

            // 关闭PDF文档和输出流
            pdfDocument.close();
            pdfOutputStream.close();
            logger.info("PDF document closed successfully.");

            // 返回PDF文件
            ByteArrayResource resource = new ByteArrayResource(pdfOutputStream.toByteArray());
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename.replace(".docx", ".pdf") + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfOutputStream.size())
                .body(resource);

        } catch (Exception e) {
            logger.error("Error converting Word to PDF: ", e);
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/pdf-to-word")
    public ResponseEntity<Resource> convertPdfToWord(@RequestParam("file") MultipartFile file) {
        try {
            // 读取PDF内容
            String text;
            try (InputStream is = file.getInputStream();
                 PDDocument pdfDocument = PDDocument.load(is)) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                text = stripper.getText(pdfDocument);
            }

            // 创建Word文档
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (XWPFDocument wordDocument = new XWPFDocument()) {
                wordDocument.createParagraph().createRun().setText(text);
                wordDocument.write(outputStream);
            }

            // 关闭输出流
            outputStream.close();

            // 返回Word文档
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalFilename().replace(".pdf", ".docx") + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .contentLength(outputStream.size())
                .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/fragment")
    public String documentConversionFragment() {
        return "document-conversion-fragment";
    }

    @GetMapping("/document-conversion")
    public String documentConversionPage() {
        return "document-conversion";
    }
} 
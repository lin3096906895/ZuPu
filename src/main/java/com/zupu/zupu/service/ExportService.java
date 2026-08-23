package com.zupu.zupu.service;

import com.zupu.zupu.entity.Family;
import com.zupu.zupu.entity.MessageBoard;
import com.zupu.zupu.entity.Person;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 数据导入导出服务
 * 使用 Apache POI 库处理 Excel 文件的读写操作
 */
@Service
public class ExportService {
    
    /**
     * 导出家族数据到 Excel
     * 
     * @param families 家族数据列表
     * @return Excel 工作簿对象
     */
    public Workbook exportFamiliesToExcel(List<Family> families) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("家族列表");
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "家族名称", "始祖名称", "状态", "创建时间", "描述"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }
        
        int rowNum = 1;
        for (Family family : families) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(family.getId());
            row.createCell(1).setCellValue(family.getName());
            row.createCell(2).setCellValue(family.getAncestorName());
            row.createCell(3).setCellValue(family.getStatus() == 1 ? "启用" : "停用");
            row.createCell(4).setCellValue(family.getCreateTime() != null ? 
                family.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "");
            row.createCell(5).setCellValue(family.getDescription());
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        return workbook;
    }
    
    /**
     * 导出人物数据到 Excel
     * 
     * @param persons 人物数据列表
     * @return Excel 工作簿对象
     */
    public Workbook exportPersonsToExcel(List<Person> persons) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("人物列表");
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "姓名", "性别", "出生日期", "逝世日期", "父亲", "简介"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }
        
        int rowNum = 1;
        for (Person person : persons) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(person.getId());
            row.createCell(1).setCellValue(person.getName());
            row.createCell(2).setCellValue(person.getGender());
            row.createCell(3).setCellValue(person.getBirthDate() != null ? 
                person.getBirthDate().toString() : "");
            row.createCell(4).setCellValue(person.getDeathDate() != null ? 
                person.getDeathDate().toString() : "");
            row.createCell(5).setCellValue(person.getFatherName());
            row.createCell(6).setCellValue(person.getBiography());
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        return workbook;
    }
    
    /**
     * 导出留言数据到 Excel
     * 
     * @param messages 留言数据列表
     * @return Excel 工作簿对象
     */
    public Workbook exportMessagesToExcel(List<MessageBoard> messages) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("留言列表");
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "标题", "分类", "内容", "附件", "作者", "发布时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }
        
        int rowNum = 1;
        for (MessageBoard message : messages) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(message.getId());
            row.createCell(1).setCellValue(message.getTitle());
            row.createCell(2).setCellValue(message.getCategory());
            row.createCell(3).setCellValue(stripHtml(message.getContent()));
            row.createCell(4).setCellValue(message.getAttachmentName() != null ? message.getAttachmentName() : "");
            row.createCell(5).setCellValue(message.getAuthorName());
            row.createCell(6).setCellValue(message.getCreateTime() != null ? 
                message.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "");
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        return workbook;
    }
    
    /**
     * 移除 HTML 标签，保留纯文本
     */
    private String stripHtml(String html) {
        if (html == null) return "";
        return html.replaceAll("<[^>]*>", "").trim();
    }
    
    /**
     * 创建表头样式（加粗、灰色背景）
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
    
    /**
     * 从 Excel 导入家族数据
     * 
     * @param file 上传的 Excel 文件
     * @return 数据列表（每个元素是一行数据的 Map）
     */
    public List<Map<String, Object>> importFamiliesFromExcel(MultipartFile file) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Map<String, Object> familyData = new HashMap<>();
                familyData.put("name", getCellValue(row.getCell(1)));
                familyData.put("ancestorName", getCellValue(row.getCell(2)));
                familyData.put("status", "启用".equals(getCellValue(row.getCell(3))) ? 1 : 0);
                familyData.put("description", getCellValue(row.getCell(5)));
                
                if (familyData.get("name") != null && !familyData.get("name").toString().isEmpty()) {
                    result.add(familyData);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 从 Excel 导入留言数据
     * 
     * @param file 上传的 Excel 文件
     * @return 数据列表（每个元素是一行数据的 Map）
     */
    public List<Map<String, Object>> importMessagesFromExcel(MultipartFile file) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Map<String, Object> messageData = new HashMap<>();
                messageData.put("title", getCellValue(row.getCell(1)));
                messageData.put("category", getCellValue(row.getCell(2)));
                messageData.put("content", getCellValue(row.getCell(3)));
                messageData.put("authorName", getCellValue(row.getCell(5)));
                
                if (messageData.get("title") != null && !messageData.get("title").toString().isEmpty()) {
                    result.add(messageData);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 获取单元格值（处理不同类型：字符串、数字、日期、布尔值）
     */
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}

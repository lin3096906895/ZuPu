package com.zupu.zupu.controller;

import com.zupu.zupu.common.Result;
import com.zupu.zupu.entity.Family;
import com.zupu.zupu.entity.Person;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.ExportService;
import com.zupu.zupu.service.FamilyService;
import com.zupu.zupu.service.PersonService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {
    private final ExportService exportService;
    private final FamilyService familyService;
    private final PersonService personService;

    public ExportController(ExportService exportService, FamilyService familyService, PersonService personService) {
        this.exportService = exportService;
        this.familyService = familyService;
        this.personService = personService;
    }

    @GetMapping("/families")
    public ResponseEntity<byte[]> exportFamilies(@AuthenticationPrincipal LoginUser user) throws IOException {
        List<Family> families = familyService.listAccessibleFamilies(user);
        Workbook workbook = exportService.exportFamiliesToExcel(families);
        byte[] bytes = writeWorkbook(workbook);
        String filename = "家族列表_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        return excelResponse(bytes, filename);
    }

    @GetMapping("/persons")
    public ResponseEntity<byte[]> exportPersons(@RequestParam Integer familyId, @AuthenticationPrincipal LoginUser user) throws IOException {
        List<Person> persons = personService.listByFamilyId(familyId, user);
        Workbook workbook = exportService.exportPersonsToExcel(persons);
        byte[] bytes = writeWorkbook(workbook);
        String filename = "人物列表_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        return excelResponse(bytes, filename);
    }

    private byte[] writeWorkbook(Workbook workbook) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        return baos.toByteArray();
    }

    private ResponseEntity<byte[]> excelResponse(byte[] bytes, String filename) {
        String encoded = java.net.URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + encoded)
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .contentLength(bytes.length)
                .body(bytes);
    }
}

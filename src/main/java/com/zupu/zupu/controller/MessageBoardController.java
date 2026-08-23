package com.zupu.zupu.controller;

import com.zupu.zupu.common.BizException;
import com.zupu.zupu.common.PageResult;
import com.zupu.zupu.common.Result;
import com.zupu.zupu.entity.MessageBoard;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.ExportService;
import com.zupu.zupu.service.MessageBoardService;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageBoardController {
    private final MessageBoardService messageBoardService;
    private final ExportService exportService;

    public MessageBoardController(MessageBoardService messageBoardService, ExportService exportService) {
        this.messageBoardService = messageBoardService;
        this.exportService = exportService;
    }

    @GetMapping
    public Result<PageResult<MessageBoard>> list(@RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        int total = messageBoardService.count();
        int offset = (pageNum - 1) * pageSize;
        List<MessageBoard> messages = messageBoardService.listByPage(offset, pageSize);
        return Result.success(new PageResult<>(messages, total, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<MessageBoard> get(@PathVariable Integer id) {
        MessageBoard message = messageBoardService.getById(id);
        if (message == null) {
            throw new BizException(404, "留言不存在");
        }
        return Result.success(message);
    }

    @PostMapping
    public Result<MessageBoard> create(@Valid @RequestBody MessageBoard message, @AuthenticationPrincipal LoginUser user) {
        message.setAuthorId(user.getId());
        message.setAuthorName(user.getDisplayName());
        message.setStatus(1);
        messageBoardService.create(message);
        return Result.success(message);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @Valid @RequestBody MessageBoard message, @AuthenticationPrincipal LoginUser user) {
        MessageBoard existing = messageBoardService.getById(id);
        if (existing == null) {
            throw new BizException(404, "留言不存在");
        }
        checkAuthor(existing, user);
        message.setId(id);
        message.setAuthorId(existing.getAuthorId());
        message.setAuthorName(existing.getAuthorName());
        message.setCreateTime(existing.getCreateTime());
        message.setStatus(1);
        messageBoardService.update(message);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id, @AuthenticationPrincipal LoginUser user) {
        MessageBoard existing = messageBoardService.getById(id);
        if (existing == null) {
            throw new BizException(404, "留言不存在");
        }
        checkAuthor(existing, user);
        messageBoardService.delete(id);
        return Result.success();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export() throws IOException {
        List<MessageBoard> messages = messageBoardService.listAll();
        Workbook workbook = exportService.exportMessagesToExcel(messages);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        String filename = "messages_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(outputStream.toByteArray());
    }

    private void checkAuthor(MessageBoard message, LoginUser user) {
        if (!isAdmin(user) && !message.getAuthorId().equals(user.getId())) {
            throw new BizException(403, "无权操作此留言");
        }
    }

    private boolean isAdmin(LoginUser user) {
        return "ADMIN".equalsIgnoreCase(user.getRole());
    }
}

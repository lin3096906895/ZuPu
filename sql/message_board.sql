-- 留言板表
CREATE TABLE IF NOT EXISTS `message_board` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(200) NOT NULL COMMENT '留言标题',
  `content` TEXT COMMENT '留言内容（富文本HTML）',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '分类：公告、讨论、求助、分享、记录、其他',
  `attachment` VARCHAR(500) DEFAULT NULL COMMENT '附件路径',
  `attachment_name` VARCHAR(200) DEFAULT NULL COMMENT '附件原始文件名',
  `author_id` INT NOT NULL COMMENT '作者用户ID',
  `author_name` VARCHAR(100) NOT NULL COMMENT '作者姓名',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1正常，0禁用',
  PRIMARY KEY (`id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_category` (`category`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言板表';

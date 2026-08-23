-- =====================================================
-- V1: 初始化图模型数据（双亲 + 婚姻 + 过继 + 世代深度）
-- 说明：本迁移会重建全部表，适合演示/种子数据环境。
-- =====================================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS message_board;
DROP TABLE IF EXISTS marriage;
DROP TABLE IF EXISTS family_event;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS generation;
DROP TABLE IF EXISTS family;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  display_name VARCHAR(50) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'USER',
  enabled TINYINT NOT NULL DEFAULT 1,
  avatar VARCHAR(255) NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE family (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  ancestor_name VARCHAR(100) NOT NULL,
  description TEXT NULL,
  owner_user_id INT NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_family_owner FOREIGN KEY (owner_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家族表';

CREATE TABLE generation (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  description VARCHAR(255) NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_generation_family FOREIGN KEY (family_id) REFERENCES family(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='世代/字辈字典表';

CREATE TABLE person (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  gender VARCHAR(10) NOT NULL DEFAULT '男',
  birth_date DATE NULL,
  death_date DATE NULL,
  birth_place VARCHAR(100) NULL,
  biography TEXT NULL,
  photo VARCHAR(255) NULL,
  father_id INT NULL,
  mother_id INT NULL,
  adoptive_father_id INT NULL,
  adoptive_mother_id INT NULL,
  generation_no INT NOT NULL DEFAULT 1,
  order_num INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_person_family FOREIGN KEY (family_id) REFERENCES family(id) ON DELETE CASCADE,
  CONSTRAINT fk_person_father FOREIGN KEY (father_id) REFERENCES person(id) ON DELETE SET NULL,
  CONSTRAINT fk_person_mother FOREIGN KEY (mother_id) REFERENCES person(id) ON DELETE SET NULL,
  CONSTRAINT fk_person_adoptive_father FOREIGN KEY (adoptive_father_id) REFERENCES person(id) ON DELETE SET NULL,
  CONSTRAINT fk_person_adoptive_mother FOREIGN KEY (adoptive_mother_id) REFERENCES person(id) ON DELETE SET NULL,
  INDEX idx_person_family (family_id),
  INDEX idx_person_father (father_id),
  INDEX idx_person_mother (mother_id),
  INDEX idx_person_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='族谱人物表';

CREATE TABLE marriage (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  husband_id INT NOT NULL,
  wife_id INT NOT NULL,
  marriage_order INT NOT NULL DEFAULT 1,
  marry_date DATE NULL,
  divorce_date DATE NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'MARRIED',
  is_primary TINYINT NOT NULL DEFAULT 1,
  note VARCHAR(255) NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_marriage_family FOREIGN KEY (family_id) REFERENCES family(id) ON DELETE CASCADE,
  CONSTRAINT fk_marriage_husband FOREIGN KEY (husband_id) REFERENCES person(id) ON DELETE CASCADE,
  CONSTRAINT fk_marriage_wife FOREIGN KEY (wife_id) REFERENCES person(id) ON DELETE CASCADE,
  INDEX idx_marriage_husband (husband_id),
  INDEX idx_marriage_wife (wife_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='婚姻关系表';

CREATE TABLE family_event (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL,
  title VARCHAR(200) NOT NULL,
  event_date DATE NULL,
  description TEXT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_event_family FOREIGN KEY (family_id) REFERENCES family(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家族事件表';

CREATE TABLE message_board (
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  content TEXT NOT NULL,
  category VARCHAR(50) NULL,
  attachment VARCHAR(255) NULL,
  attachment_name VARCHAR(255) NULL,
  author_id INT NULL,
  author_name VARCHAR(50) NULL,
  status INT NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_msg_author (author_id),
  INDEX idx_msg_category (category),
  INDEX idx_msg_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言板表';

-- ============ 种子数据 ============

INSERT INTO users (id, username, password_hash, display_name, role, enabled)
VALUES
  (1, 'admin', 'ac0e7d037817094e9e0b4441f9bae3209d67b02fa484917065f71b16109a1a78', '系统管理员', 'ADMIN', 1),
  (2, 'demo', 'ac0e7d037817094e9e0b4441f9bae3209d67b02fa484917065f71b16109a1a78', '演示用户', 'USER', 1);

INSERT INTO family (id, name, ancestor_name, description, owner_user_id, status)
VALUES
  (1, '安氏家谱', '安世祖', '示例家族数据，四代同堂，展示双亲/婚姻/过继图模型。', 1, 1),
  (2, '陈氏家谱', '陈开基', '演示普通用户维护自己的家族档案。', 2, 1);

INSERT INTO generation (id, family_id, title, sort_order, description)
VALUES
  (1, 1, '第一世', 1, '立族开基'),
  (2, 1, '第二世', 2, '传承拓展'),
  (3, 1, '第三世', 3, '家业兴旺'),
  (4, 1, '第四世', 4, '人才辈出'),
  (5, 2, '第一世', 1, '陈氏始迁祖'),
  (6, 2, '第二世', 2, '陈氏承续');

-- 人物（father_id/mother_id 建立双亲关系；配偶作为独立 person，经 marriage 关联）
INSERT INTO person (id, family_id, name, gender, birth_date, death_date, birth_place, father_id, mother_id, generation_no, order_num, biography)
VALUES
  -- 安氏 第一世
  (1, 1, '安世祖', '男', '1940-05-12', '2008-11-03', '山东济南', NULL, NULL, 1, 1, '安氏家族始祖，奠定家风家训。'),
  (10, 1, '赵氏', '女', '1943-09-18', '2012-02-15', NULL, NULL, NULL, 1, 0, '安世祖之妻。'),
  -- 安氏 第二世
  (2, 1, '安国栋', '男', '1964-02-18', NULL, NULL, 1, 10, 2, 1, '长子，负责宗族事务整理。'),
  (3, 1, '安国梁', '男', '1968-09-25', NULL, NULL, 1, 10, 2, 2, '次子，长期参与家谱修订。'),
  (11, 1, '李氏', '女', '1966-07-02', NULL, NULL, NULL, NULL, 2, 0, '安国栋之妻。'),
  (12, 1, '王氏', '女', '1971-04-11', NULL, NULL, NULL, NULL, 2, 0, '安国梁之妻。'),
  -- 安氏 第三世
  (4, 1, '安文博', '男', '1988-07-10', NULL, NULL, 2, 11, 3, 1, '第三世代表人物，专注数字化族谱建设。'),
  (5, 1, '安文雅', '女', '1990-03-11', NULL, NULL, 2, 11, 3, 2, '从事教育工作。'),
  (20, 1, '安文轩', '男', '1992-01-30', NULL, NULL, 2, 11, 3, 3, '经商，常驻海外。'),
  (6, 1, '安志远', '男', '1993-12-22', NULL, NULL, 3, 12, 3, 1, '工程师。'),
  (21, 1, '安志明', '男', '1996-08-05', NULL, NULL, 3, 12, 3, 2, '医生。'),
  (13, 1, '周静', '女', '1989-06-14', NULL, NULL, NULL, NULL, 3, 0, '安文博之妻。'),
  (22, 1, '陈氏', '女', '1993-10-08', NULL, NULL, NULL, NULL, 3, 0, '安文轩之妻。'),
  (14, 1, '许婷', '女', '1994-02-19', NULL, NULL, NULL, NULL, 3, 0, '安志远之妻。'),
  (23, 1, '刘氏', '女', '1997-12-01', NULL, NULL, NULL, NULL, 3, 0, '安志明之妻。'),
  -- 安氏 第四世
  (7, 1, '安启明', '男', '2015-08-03', NULL, NULL, 4, 13, 4, 1, '第四世晚辈。'),
  (24, 1, '安启航', '男', '2018-01-21', NULL, NULL, 4, 13, 4, 2, '第四世晚辈。'),
  (25, 1, '安启峰', '男', '2016-05-17', NULL, NULL, 20, 22, 4, 1, '第四世晚辈。'),
  (26, 1, '安启辰', '男', '2017-09-09', NULL, NULL, 6, 14, 4, 1, '第四世晚辈。'),
  (27, 1, '安启辉', '男', '2019-03-28', NULL, NULL, 21, 23, 4, 1, '第四世晚辈。'),
  (28, 1, '安启雯', '女', '2019-03-28', NULL, NULL, 21, 23, 4, 2, '第四世晚辈，龙凤胎。'),
  -- 陈氏
  (8, 2, '陈开基', '男', '1950-01-15', NULL, '福建泉州', NULL, NULL, 1, 1, '陈氏家谱始祖。'),
  (9, 2, '陈守正', '男', '1976-06-21', NULL, NULL, 8, 15, 2, 1, '陈氏第二世人物。'),
  (34, 2, '陈文轩', '男', '2001-11-02', NULL, NULL, 9, 16, 3, 1, '陈氏第三世。'),
  (15, 2, '吴氏', '女', '1953-08-30', NULL, NULL, NULL, NULL, 1, 0, '陈开基之妻。'),
  (16, 2, '孙梅', '女', '1978-05-12', NULL, NULL, NULL, NULL, 2, 0, '陈守正之妻。');

-- 婚姻关系
INSERT INTO marriage (family_id, husband_id, wife_id, marriage_order, status, is_primary)
VALUES
  (1, 1, 10, 1, 'MARRIED', 1),
  (1, 2, 11, 1, 'MARRIED', 1),
  (1, 3, 12, 1, 'MARRIED', 1),
  (1, 4, 13, 1, 'MARRIED', 1),
  (1, 20, 22, 1, 'MARRIED', 1),
  (1, 6, 14, 1, 'MARRIED', 1),
  (1, 21, 23, 1, 'MARRIED', 1),
  (2, 8, 15, 1, 'MARRIED', 1),
  (2, 9, 16, 1, 'MARRIED', 1);

SET FOREIGN_KEY_CHECKS = 1;

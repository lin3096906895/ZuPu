-- ============================================================
-- 【遗留文件 / LEGACY】请勿用于新部署
--
-- 这是项目早期设计的建表脚本，与当前实现不一致：
--   * 人物只支持单亲（parent_id），现为 father_id / mother_id
--   * 配偶内联在 person.spouse_name，现为独立 marriage 表
--   * 含已废弃的 person_tree 表
--
-- 当前表结构由 Flyway 管理，见 src/main/resources/db/migration/
-- 仅作历史参考保留。
-- ============================================================

CREATE DATABASE IF NOT EXISTS zupu
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE zupu;

DROP TABLE IF EXISTS person_tree;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS generation;
DROP TABLE IF EXISTS family;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
  password_hash VARCHAR(128) NOT NULL COMMENT 'SHA-256 加密后的密码',
  display_name VARCHAR(50) NOT NULL COMMENT '展示昵称',
  role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN / USER',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE family (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL COMMENT '家族名称',
  ancestor_name VARCHAR(100) NOT NULL COMMENT '始祖/家族代表人物',
  description TEXT NULL COMMENT '家族简介',
  owner_user_id INT NOT NULL COMMENT '创建人',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1 启用，0 禁用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_family_owner FOREIGN KEY (owner_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家族表';

CREATE TABLE generation (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL COMMENT '所属家族',
  title VARCHAR(100) NOT NULL COMMENT '世代名称',
  sort_order INT NOT NULL COMMENT '世代顺序',
  description VARCHAR(255) NULL COMMENT '世代备注',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_generation_family FOREIGN KEY (family_id) REFERENCES family(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='世代表';

CREATE TABLE person (
  id INT PRIMARY KEY AUTO_INCREMENT,
  family_id INT NOT NULL COMMENT '所属家族',
  generation_id INT NOT NULL COMMENT '所属世代',
  parent_id INT NULL COMMENT '父节点人物',
  name VARCHAR(100) NOT NULL COMMENT '姓名',
  gender VARCHAR(10) NOT NULL DEFAULT '男' COMMENT '性别',
  birth_date DATE NULL COMMENT '出生日期',
  death_date DATE NULL COMMENT '逝世日期',
  spouse_name VARCHAR(100) NULL COMMENT '配偶姓名',
  biography TEXT NULL COMMENT '人物简介',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1 在谱，0 停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_person_family FOREIGN KEY (family_id) REFERENCES family(id) ON DELETE CASCADE,
  CONSTRAINT fk_person_generation FOREIGN KEY (generation_id) REFERENCES generation(id),
  CONSTRAINT fk_person_parent FOREIGN KEY (parent_id) REFERENCES person(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='族谱人物表';

INSERT INTO users (id, username, password_hash, display_name, role, enabled)
VALUES
  (1, 'admin', 'ac0e7d037817094e9e0b4441f9bae3209d67b02fa484917065f71b16109a1a78', '系统管理员', 'ADMIN', 1),
  (2, 'demo', 'ac0e7d037817094e9e0b4441f9bae3209d67b02fa484917065f71b16109a1a78', '演示用户', 'USER', 1);

INSERT INTO family (id, name, ancestor_name, description, owner_user_id, status)
VALUES
  (1, '安氏家谱', '安世祖', '示例家族数据，可直接用于演示树状图与文档树。', 1, 1),
  (2, '陈氏家谱', '陈开基', '演示普通用户维护自己的家族档案。', 2, 1);

INSERT INTO generation (id, family_id, title, sort_order, description)
VALUES
  (1, 1, '第一世', 1, '立族开基'),
  (2, 1, '第二世', 2, '传承拓展'),
  (3, 1, '第三世', 3, '家业兴旺'),
  (4, 1, '第四世', 4, '人才辈出'),
  (5, 2, '第一世', 1, '陈氏始迁祖'),
  (6, 2, '第二世', 2, '陈氏承续');

INSERT INTO person (id, family_id, generation_id, parent_id, name, gender, birth_date, spouse_name, biography, status)
VALUES
  (1, 1, 1, NULL, '安世祖', '男', '1940-05-12', '赵氏', '安氏家族始祖，奠定家风家训。', 1),
  (2, 1, 2, 1, '安国栋', '男', '1964-02-18', '李氏', '长子，负责宗族事务整理。', 1),
  (3, 1, 2, 1, '安国梁', '男', '1968-09-25', '王氏', '次子，长期参与家谱修订。', 1),
  (4, 1, 3, 2, '安文博', '男', '1988-07-10', '周静', '第三世代表人物之一。', 1),
  (5, 1, 3, 2, '安文雅', '女', '1990-03-11', NULL, '从事教育工作。', 1),
  (6, 1, 3, 3, '安志远', '男', '1993-12-22', '许婷', '专注数字化族谱建设。', 1),
  (7, 1, 4, 4, '安启明', '男', '2015-08-03', NULL, '第四世晚辈。', 1),
  (8, 2, 5, NULL, '陈开基', '男', '1950-01-15', '吴氏', '陈氏家谱始祖。', 1),
  (9, 2, 6, 8, '陈守正', '男', '1976-06-21', '孙梅', '陈氏第二世人物。', 1);

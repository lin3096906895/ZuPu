-- V5: 补充第五、六世数据，用于演示「五世一图」分段

-- 第四世配偶
INSERT INTO person (id, family_id, name, gender, birth_place, generation_no, order_num, biography)
VALUES
  (35, 1, '何氏', '女', '广东省深圳市南山区', 4, 0, '安启明之妻。'),
  (36, 1, '苏氏', '女', '广东省深圳市南山区', 4, 0, '安启辉之妻。');

-- 第五世
INSERT INTO person (id, family_id, name, gender, birth_place, father_id, mother_id, generation_no, order_num, biography)
VALUES
  (37, 1, '安承宇', '男', '广东省深圳市南山区', 7, 35, 5, 1, '第五世。'),
  (38, 1, '安承轩', '男', '广东省深圳市南山区', 27, 36, 5, 1, '第五世。'),
  (39, 1, '安承欣', '女', '广东省深圳市南山区', 27, 36, 5, 2, '第五世，龙凤胎。'),
  (40, 1, '安承泽', '男', '广东省深圳市南山区', 25, NULL, 5, 1, '第五世。'),
  (41, 1, '安承霖', '男', '广东省深圳市南山区', 26, NULL, 5, 1, '第五世。');

-- 第六世
INSERT INTO person (id, family_id, name, gender, birth_place, father_id, mother_id, generation_no, order_num, biography)
VALUES
  (42, 1, '安志贤', '男', '广东省深圳市南山区', 37, NULL, 6, 1, '第六世。'),
  (43, 1, '安志琳', '女', '广东省深圳市南山区', 38, NULL, 6, 1, '第六世。');

-- 新增婚姻
INSERT INTO marriage (family_id, husband_id, wife_id, marriage_order, status, is_primary)
VALUES
  (1, 7, 35, 1, 'MARRIED', 1),
  (1, 27, 36, 1, 'MARRIED', 1);

-- V4: 补齐缺失的出生地
UPDATE person SET birth_place = '北京市海淀区' WHERE id = 5;      -- 安文雅（随父母）
UPDATE person SET birth_place = '山东省济南市章丘区' WHERE id = 10; -- 赵氏（随始祖）
UPDATE person SET birth_place = '北京市海淀区' WHERE id = 11;      -- 李氏（随安国栋）
UPDATE person SET birth_place = '北京市海淀区' WHERE id = 12;      -- 王氏（随安国梁）
UPDATE person SET birth_place = '福建省泉州市鲤城区' WHERE id = 15; -- 吴氏（随陈开基）

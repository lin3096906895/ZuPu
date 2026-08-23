-- V3: 出生地细化到区县级
UPDATE person SET birth_place = '山东省济南市章丘区' WHERE id = 1;
UPDATE person SET birth_place = '北京市海淀区' WHERE id IN (2, 3, 6, 14);
UPDATE person SET birth_place = '上海市浦东新区' WHERE id IN (4, 13);
UPDATE person SET birth_place = '广东省广州市天河区' WHERE id IN (20, 22);
UPDATE person SET birth_place = '浙江省杭州市西湖区' WHERE id IN (21, 23);
UPDATE person SET birth_place = '广东省深圳市南山区' WHERE id IN (7, 24, 25, 26, 27, 28);
UPDATE person SET birth_place = '福建省泉州市鲤城区' WHERE id IN (8, 9, 16, 34);

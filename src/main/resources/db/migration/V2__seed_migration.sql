-- V2: 为种子人物补充出生地，展示家族迁徙脉络
UPDATE person SET birth_place = '北京' WHERE id IN (2, 3);
UPDATE person SET birth_place = '上海' WHERE id IN (4, 13);
UPDATE person SET birth_place = '广州' WHERE id IN (20, 22);
UPDATE person SET birth_place = '北京' WHERE id IN (6, 14);
UPDATE person SET birth_place = '杭州' WHERE id IN (21, 23);
UPDATE person SET birth_place = '深圳' WHERE id IN (7, 24, 25, 26, 27, 28);
UPDATE person SET birth_place = '福建泉州' WHERE id IN (9, 16, 34);

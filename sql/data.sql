-- 插入默认用户
INSERT INTO `drug_inventory`.`t_user` (`id`, `user_account`, `user_name`, `phone`, `avatar`, `password`, `is_deleted`,
                                       `create_time`, `update_time`)
VALUES (1, 'admin', '管理员1', '16651321582', 'https://qimuovo-picgo.oss-cn-guangzhou.aliyuncs.com/note/OIP%20(1).jpg',
        '65e3f22c60b794fb85933d662449b3e2', 0, '2026-04-24 22:56:44', '2026-04-24 22:57:53');


-- 插入生产厂家15条真实数据
INSERT INTO t_manufacturer (id, manufacturer_name, contact_person, phone, address, is_deleted)
VALUES (1, '广州白云山制药有限公司', '李强', '13800138001', '广东省广州市白云区', 0),
       (2, '江苏恒瑞医药股份有限公司', '王磊', '13800138002', '江苏省连云港市', 0),
       (3, '石药集团有限公司', '张敏', '13800138003', '河北省石家庄市', 0),
       (4, '扬子江药业集团有限公司', '刘洋', '13800138004', '江苏省泰州市', 0),
       (5, '华润三九医药股份有限公司', '陈杰', '13800138005', '广东省深圳市', 0),
       (6, '中国医药集团有限公司', '赵峰', '13800138006', '北京市海淀区', 0),
       (7, '复星医药集团', '周凯', '13800138007', '上海市浦东新区', 0),
       (8, '云南白药集团股份有限公司', '孙伟', '13800138008', '云南省昆明市', 0),
       (9, '同仁堂股份有限公司', '吴斌', '13800138009', '北京市东城区', 0),
       (10, '哈药集团有限公司', '郑鹏', '13800138010', '黑龙江省哈尔滨市', 0),
       (11, 'Pfizer Inc.', 'John Smith', '+1-202-555-0101', 'New York, USA', 0),
       (12, 'Novartis AG', 'Anna Müller', '+41-61-324-1111', 'Basel, Switzerland', 0),
       (13, 'Roche Holding AG', 'Peter Weber', '+41-61-688-1111', 'Basel, Switzerland', 0),
       (14, 'Bayer AG', 'Thomas Schmidt', '+49-214-301', 'Leverkusen, Germany', 0),
       (15, 'Johnson & Johnson', 'Emily Davis', '+1-732-524-0400', 'New Jersey, USA', 0);


-- 插入15条药品真实数据
INSERT INTO t_drug (id, drug_name, drug_code, specification, manufacturer_id, is_deleted)
VALUES (1, '阿莫西林胶囊', 'DRUG001', '0.25g*24粒', 1, 0),
       (2, '布洛芬缓释胶囊', 'DRUG002', '0.3g*20粒', 2, 0),
       (3, '对乙酰氨基酚片', 'DRUG003', '0.5g*12片', 3, 0),
       (4, '头孢克肟片', 'DRUG004', '0.1g*6片', 4, 0),
       (5, '维生素C片', 'DRUG005', '100mg*100片', 5, 0),
       (6, '甲硝唑片', 'DRUG006', '0.2g*24片', 6, 0),
       (7, '奥美拉唑肠溶胶囊', 'DRUG007', '20mg*14粒', 7, 0),
       (8, '氯雷他定片', 'DRUG008', '10mg*10片', 8, 0),
       (9, '双黄连口服液', 'DRUG009', '10ml*10支', 9, 0),
       (10, '阿司匹林肠溶片', 'DRUG010', '100mg*30片', 10, 0),
       (11, 'Lipitor（阿托伐他汀）', 'DRUG011', '20mg*7片', 11, 0),
       (12, 'Diovan（缬沙坦）', 'DRUG012', '80mg*14片', 12, 0),
       (13, 'Herceptin（曲妥珠单抗）', 'DRUG013', '440mg/瓶', 13, 0),
       (14, 'Aspirin Bayer', 'DRUG014', '100mg*20片', 14, 0),
       (15, 'Tylenol（对乙酰氨基酚）', 'DRUG015', '500mg*16片', 15, 0);
/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : person_blog

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 07/09/2025 10:32:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for circle_image
-- ----------------------------
DROP TABLE IF EXISTS `circle_image`;
CREATE TABLE `circle_image`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `circle_id` int NULL DEFAULT NULL COMMENT '对应sys_circle对应',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sort` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of circle_image
-- ----------------------------
INSERT INTO `circle_image` VALUES (16, 23, 'https://cdn.yuanshikong.net/NewTab/wallpaper/itab/6486aeda10f2b458c8669211.jpe?imageMogr2/thumbnail/450x300', '1');
INSERT INTO `circle_image` VALUES (17, 23, 'https://cdn.yuanshikong.net/NewTab/wallpaper/itab/6486aea910f2b458c86677cd.jpe?imageMogr2/thumbnail/450x300', '2');
INSERT INTO `circle_image` VALUES (18, 23, 'https://cdn.yuanshikong.net/NewTab/wallpaper/itab/645324af62269728166a721e.jpe?imageMogr2/thumbnail/450x300', '3');
INSERT INTO `circle_image` VALUES (19, 1, 'http://localhost:9090/file/c769591a261c432c8a49da7a060a4c49.webp', '1');
INSERT INTO `circle_image` VALUES (20, 2, 'http://localhost:9090/file/e06d8e900ba744a89838d12d2788f8de.jpg', '1');
INSERT INTO `circle_image` VALUES (21, 2, 'http://localhost:9090/file/c769591a261c432c8a49da7a060a4c49.webp', '2');
INSERT INTO `circle_image` VALUES (22, 2, 'http://localhost:9090/file/5cb2fb6e4c654675817ec8e464c99ec2.jpg', '3');
INSERT INTO `circle_image` VALUES (23, 2, 'http://localhost:9090/file/e06d8e900ba744a89838d12d2788f8de.jpg', '4');
INSERT INTO `circle_image` VALUES (24, 2, 'http://localhost:9090/file/e06d8e900ba744a89838d12d2788f8de.jpg', '5');
INSERT INTO `circle_image` VALUES (25, 24, 'http://localhost:9090/file/e06d8e900ba744a89838d12d2788f8de.jpg', '1');
INSERT INTO `circle_image` VALUES (26, 24, 'http://localhost:9090/file/e06d8e900ba744a89838d12d2788f8de.jpg', '2');

-- ----------------------------
-- Table structure for concern_info
-- ----------------------------
DROP TABLE IF EXISTS `concern_info`;
CREATE TABLE `concern_info`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '书籍/电影标题',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片URL',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详情链接',
  `theme` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型：book-书籍，film-电影',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of concern_info
-- ----------------------------
INSERT INTO `concern_info` VALUES (1, '解忧杂货铺', 'http://localhost:9090/file/5cb2fb6e4c654675817ec8e464c99ec2.jpg', 'https://book.douban.com/subject/25862578/', 'book', '2025-08-22 11:19:44');
INSERT INTO `concern_info` VALUES (2, '假如爱有天意', 'http://localhost:9090/file/4fbe70a2dda644749240af59f5bda1e1.webp', 'https://movie.douban.com/subject/1304073/', 'film', '2025-08-22 11:19:44');
INSERT INTO `concern_info` VALUES (3, '肖申克救赎', 'http://localhost:9090/file/c769591a261c432c8a49da7a060a4c49.webp', 'https://movie.douban.com/subject/1304073/', 'film', '2025-08-22 11:25:41');

-- ----------------------------
-- Table structure for sys_apply
-- ----------------------------
DROP TABLE IF EXISTS `sys_apply`;
CREATE TABLE `sys_apply`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  `link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '连接',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_apply
-- ----------------------------
INSERT INTO `sys_apply` VALUES (1, '豆包', 'https://doubao.com', 'chrome-extension://dbjibobgilijgolhjdcbdebjhejelffo/assets/icon.png', '2025-08-30 11:17:11');
INSERT INTO `sys_apply` VALUES (2, 'B站', 'https://www.bilibili.com/', 'https://cdn.yuanshikong.net/NewTab/icons/61bc4a2410915ef5fe36eb8d.svg', '2025-08-30 11:17:44');
INSERT INTO `sys_apply` VALUES (3, '知乎', 'https://www.zhihu.com/', 'https://cdn.yuanshikong.net/NewTab/icons/60b9ea6fae5a9ba4024b36ec.svg', '2025-08-30 11:18:17');
INSERT INTO `sys_apply` VALUES (4, 'CSDN', 'https://www.csdn.net/', 'https://cdn.yuanshikong.net/NewTab/icons/60b9ea71ae5a9ba4024b36fe.svg', '2025-08-30 11:18:32');
INSERT INTO `sys_apply` VALUES (5, '抖音', 'https://www.douyin.com/', 'https://cdn.yuanshikong.net/NewTab/icons/60b9ea8eae5a9ba4024b37cb.svg', '2025-08-30 11:18:47');
INSERT INTO `sys_apply` VALUES (6, 'QQ音乐', 'https://y.qq.com/', 'https://cdn.yuanshikong.net/NewTab/icons/60b9ea76ae5a9ba4024b371f.svg', '2025-08-30 11:19:02');
INSERT INTO `sys_apply` VALUES (8, '163邮箱', 'https://mail.163.com/', 'https://cdn.yuanshikong.net/NewTab/icons/60b9ea71ae5a9ba4024b3701.svg', '2025-08-30 11:19:37');
INSERT INTO `sys_apply` VALUES (9, 'QQ邮箱', 'https://mail.qq.com/', 'https://cdn.yuanshikong.net/NewTab/icons/60b9ea70ae5a9ba4024b36f3.svg', '2025-08-30 11:19:50');
INSERT INTO `sys_apply` VALUES (10, '百度翻译', 'https://fanyi.baidu.com/', 'https://cdn.yuanshikong.net/NewTab/icons/60b9ea95ae5a9ba4024b37ff.svg', '2025-08-30 11:20:07');
INSERT INTO `sys_apply` VALUES (11, '知网', 'https://www.cnki.net/', 'https://cdn.yuanshikong.net/NewTab/icons/60b9ea72ae5a9ba4024b3709.svg', '2025-08-30 11:20:19');
INSERT INTO `sys_apply` VALUES (12, 'X-MOL', 'https://www.x-mol.com/chem', 'https://cdn.yuanshikong.net/NewTab/icons/60b9ee90ae5a9ba4024b455c.png', '2025-08-30 11:20:33');
INSERT INTO `sys_apply` VALUES (13, '文献互助', 'https://www.ablesci.com/', 'http://www.ablesci.com/favicon.ico', '2025-08-30 11:20:47');
INSERT INTO `sys_apply` VALUES (14, 'GitHub', 'https://github.com/datamonday', 'https://cdn.yuanshikong.net/NewTab/icons/60b9ea6fae5a9ba4024b36ed.svg', '2025-08-30 11:20:59');
INSERT INTO `sys_apply` VALUES (15, '阿里云', 'https://account.aliyun.com/', 'https://cdn.yuanshikong.net/NewTab/icons/60b9ea7aae5a9ba4024b3744.svg', '2025-08-30 11:21:12');
INSERT INTO `sys_apply` VALUES (16, 'Papers With Code', 'https://paperswithcode.com/', 'https://cdn.yuanshikong.net/NewTab/icons/60b9eeb8ae5a9ba4024b45e1.png', '2025-08-30 11:21:26');
INSERT INTO `sys_apply` VALUES (17, '网飞猫', 'https://www.ncat22.com/', 'https://vf.cfaqcgj.com/vod_pc_static_ncat/images/favicon.ico?ver=250711', '2025-08-30 11:21:38');

-- ----------------------------
-- Table structure for sys_bgimage
-- ----------------------------
DROP TABLE IF EXISTS `sys_bgimage`;
CREATE TABLE `sys_bgimage`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '链接',
  `thumbnail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '缩略图',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_bgimage
-- ----------------------------
INSERT INTO `sys_bgimage` VALUES (1, '水漫窗户', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/62552700452009fb7a5febf2.jpg', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/62552700452009fb7a5febf2.jpg?imageMogr2/thumbnail/450x300');
INSERT INTO `sys_bgimage` VALUES (6, '精灵之泪', 'https://cdn.yuanshikong.net/NewTab/wallpaper/itab/645324c962269728166a7246.jpe?imageMogr2', 'https://cdn.yuanshikong.net/NewTab/wallpaper/itab/645324c962269728166a7246.jpe?imageMogr2/thumbnail/450x300');
INSERT INTO `sys_bgimage` VALUES (7, '静谧之章', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/625526ff452009fb7a5febb6.jpg', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/625526ff452009fb7a5febb6.jpg?imageMogr2/thumbnail/450x300');
INSERT INTO `sys_bgimage` VALUES (8, '湖心小屋', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/62552700452009fb7a5febe4_new.jpg?imageMogr2', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/62552700452009fb7a5febe4_new.jpg?imageMogr2/thumbnail/450x300');
INSERT INTO `sys_bgimage` VALUES (9, '落日余晖', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/644b3833be4db93bda03c2e2.jpg?imageMogr2', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/644b3833be4db93bda03c2e2.jpg?imageMogr2/thumbnail/450x300');
INSERT INTO `sys_bgimage` VALUES (10, '不良人', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/625526fe452009fb7a5feb92.jpg?imageMogr2', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/625526fe452009fb7a5feb92.jpg?imageMogr2/thumbnail/450x300');
INSERT INTO `sys_bgimage` VALUES (11, '工作时光', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/625526ff452009fb7a5febd4.jpg?imageMogr2', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/625526ff452009fb7a5febd4.jpg?imageMogr2/thumbnail/450x300');
INSERT INTO `sys_bgimage` VALUES (12, '动漫', '', 'http://localhost:9090/file/06a48acf78d841cca284496aa1347584.jpg');

-- ----------------------------
-- Table structure for sys_blog
-- ----------------------------
DROP TABLE IF EXISTS `sys_blog`;
CREATE TABLE `sys_blog`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `picture` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
  `information` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  `badge` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `pageview` int NULL DEFAULT NULL COMMENT '浏览量',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_blog
-- ----------------------------
INSERT INTO `sys_blog` VALUES (3, '兄弟呢', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/625526ff452009fb7a5febb2_new.jpg?imageMogr2/thumbnail/450x300', '# 我的兄弟呢\n\n## 我的兄弟呢\n\n我的兄弟呢', 'POTATO', '兄弟', 12, '2025-09-03 10:44:26');
INSERT INTO `sys_blog` VALUES (4, '1', '', '1', 'POTATO', '', 0, '2025-09-06 20:57:13');

-- ----------------------------
-- Table structure for sys_circle
-- ----------------------------
DROP TABLE IF EXISTS `sys_circle`;
CREATE TABLE `sys_circle`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `image_list` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片列表',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数量',
  `has_liked` tinyint(1) NULL DEFAULT 0 COMMENT '是否喜欢',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_circle
-- ----------------------------
INSERT INTO `sys_circle` VALUES (1, '今天是什么日子呢？😍', 'http://localhost:9090/file/e06d8e900ba744a89838d12d2788f8de.jpg,http://localhost:9090/file/5cb2fb6e4c654675817ec8e464c99ec2.jpg,http://localhost:9090/file/c769591a261c432c8a49da7a060a4c49.webp', 17, NULL, '2025-08-23 12:47:44');
INSERT INTO `sys_circle` VALUES (2, '今天我怎么这么累呢？😂', NULL, 25, NULL, '2025-08-24 12:41:46');
INSERT INTO `sys_circle` VALUES (23, '今天好开心，发工资了😃', NULL, 2, NULL, '2025-09-04 13:33:09');
INSERT INTO `sys_circle` VALUES (24, '1111😁', NULL, 2, NULL, '2025-09-04 13:33:41');

-- ----------------------------
-- Table structure for sys_concern
-- ----------------------------
DROP TABLE IF EXISTS `sys_concern`;
CREATE TABLE `sys_concern`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主页描述',
  `age` int NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `hobby` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `homedesc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `profile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人简介',
  `prose` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '我的散文',
  `skill` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '我的技巧',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_concern
-- ----------------------------
INSERT INTO `sys_concern` VALUES (1, 'POTATO', 24, '2474921543@qq.com', '篮球，健身，看书，玩乐', 'http://localhost:9090/file/e06d8e900ba744a89838d12d2788f8de.jpg', '[\"欢迎访问我的个人主页，这里展示了我的个人信息和兴趣爱好。\",\"通过左侧导航可以浏览不同分类的内容\"]', '[\"\'small potato\' 意为小人物。通信工程小破硕。\",\"性格介于开朗和沉默之间，热爱生活。\",\"如果自己还不够强大，就得把自己藏起来不断学习。学历阅历能力都是铺垫，在无人问津的地方练、在万众瞩目的地方出现，时间就是最好的证明。\"]', '[\"写什么呢？写我不知上进，写我贪玩勿学，写我缺乏志向，写我蹉跎二十余载，也未学一技之长傍身，终日违心陪笑，苟且于市井之间，写我多年八方奔跑，跌跌撞撞一事无成，还是写花有重开日，人无再少年？\"]', '[\"编程语言：JavaScript、Vue、Python、C++\",\"设计工具：PS、PR\"]', '2025-09-05 12:02:53');

-- ----------------------------
-- Table structure for sys_diary
-- ----------------------------
DROP TABLE IF EXISTS `sys_diary`;
CREATE TABLE `sys_diary`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `picture` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
  `information` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  `badge` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `pageview` int NULL DEFAULT NULL COMMENT '浏览量',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_diary
-- ----------------------------
INSERT INTO `sys_diary` VALUES (25, '兄弟', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/625526ff452009fb7a5febba_new.jpg?imageMogr2/thumbnail/450x300', '# 什么是兄弟\n## 兄弟\n没事可以不联系，有事联系就能帮忙的\n\n## 有吗\n有的有的\n------------------------------------\n今天好开心哦！！！哈哈哈哈', 'POTATO', '兄弟', 4, '2025-09-03 10:31:09');

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` int(10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型',
  `size` bigint NULL DEFAULT NULL COMMENT '文件大小',
  `url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '下载链接',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否禁用链接',
  `md5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_file
-- ----------------------------
INSERT INTO `sys_file` VALUES (0000000003, 'personalAvatar.jpg', 'jpg', 110, 'http://localhost:9090/file/e06d8e900ba744a89838d12d2788f8de.jpg', 0, 1, '0efa68962eedff8d04fd04fbcc0b8ba4');
INSERT INTO `sys_file` VALUES (0000000005, 'film_1.webp', 'webp', 21, 'http://localhost:9090/file/c769591a261c432c8a49da7a060a4c49.webp', 0, 1, '53a312525af023322a27fb1866334eba');
INSERT INTO `sys_file` VALUES (0000000006, 'book_1.1c3a9138.jpg', 'jpg', 25, 'http://localhost:9090/file/5cb2fb6e4c654675817ec8e464c99ec2.jpg', 0, 1, 'bfd5c7cb9b657bbc8fad3e634819eb2a');
INSERT INTO `sys_file` VALUES (0000000007, 'film_2.webp', 'webp', 11, 'http://localhost:9090/file/4fbe70a2dda644749240af59f5bda1e1.webp', 0, 1, '727004e9058ed984ba3162ba1c87f09c');
INSERT INTO `sys_file` VALUES (0000000008, '美丽的神话-MusicEnc.lrc', 'lrc', 1, 'http://localhost:9090/file/132be88b545a4cb799188177f931dc9a.lrc', 0, 1, '26ee336846ee6714b9e0ef9585f78816');
INSERT INTO `sys_file` VALUES (0000000009, '大城小爱 - 梓渝.lrc', 'lrc', 2, 'http://localhost:9090/file/8f8875cb20c645999c0a63f55668b186.lrc', 0, 1, '94409c91cc62348b0b2bbfc29ee45010');
INSERT INTO `sys_file` VALUES (0000000010, 'desktop.jpg', 'jpg', 371, 'http://localhost:9090/file/06a48acf78d841cca284496aa1347584.jpg', 0, 1, '35f6e87cbd6bc88ad284b4d4a4761333');

-- ----------------------------
-- Table structure for sys_loveline
-- ----------------------------
DROP TABLE IF EXISTS `sys_loveline`;
CREATE TABLE `sys_loveline`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_loveline
-- ----------------------------
INSERT INTO `sys_loveline` VALUES (1, '哈哈哈哈', '2025-08-28 13:49:32', 'POTATO');
INSERT INTO `sys_loveline` VALUES (3, '结束啦！这个班到头了，不用早起或者熬夜啦。嘻嘻嘻', '2025-09-05 17:06:51', 'POTATO');
INSERT INTO `sys_loveline` VALUES (4, '明天就回家，开心', '2025-09-05 22:30:37', 'POTATO');

-- ----------------------------
-- Table structure for sys_lovemessage
-- ----------------------------
DROP TABLE IF EXISTS `sys_lovemessage`;
CREATE TABLE `sys_lovemessage`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容',
  `author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_lovemessage
-- ----------------------------
INSERT INTO `sys_lovemessage` VALUES (1, '123', '', '2025-08-28 13:49:40');
INSERT INTO `sys_lovemessage` VALUES (3, '请你继续完善啊！', '陌生人', '2025-09-05 16:57:49');

-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  `theme` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主题',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容',
  `contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'love中的留言\r\n' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_message
-- ----------------------------
INSERT INTO `sys_message` VALUES (7, '陌生人', '建议', '询问', '111', '2025-08-19 10:33:44');
INSERT INTO `sys_message` VALUES (9, '陌生人', '联系', '联系下我', '', '2025-08-19 10:36:11');
INSERT INTO `sys_message` VALUES (13, '陌生人', '建议', '你还没有做完哦，请你继续做', '13547667543', '2025-09-04 23:26:00');

-- ----------------------------
-- Table structure for sys_music
-- ----------------------------
DROP TABLE IF EXISTS `sys_music`;
CREATE TABLE `sys_music`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `song_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '歌曲名称',
  `singer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '歌手',
  `album` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专辑',
  `url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '歌曲源地址',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '音乐图片',
  `lyric` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '歌词',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_music
-- ----------------------------
INSERT INTO `sys_music` VALUES (1, '美丽的神话', '韩红&孙楠', '恋曲神话', '\r\nhttps://m801.music.126.net/20250905195821/895fb2db9d1c073e8b4d7a7e49dc7a00/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/45693210730/af8d/7664/442c/b54e4319b99a572bbbb628fb3cb45c1b.mp3?vuutv=Y5RnmiD1bhZzkaZi/J5t+veSu/erLFfPYQnvAanEfiutZUCgspNSe52fSRTndo01GdROeWUbPutkNqgr/H+ukLz1NL/sLpVIXspnQfKOzs7H/6URU76fzax3HcFrbUPXf/O8/VNUwJK4kaH60Eat6+vb0q/+mUIohQxPKXC8S/HdgJYMP3vXVrCaEtCHoszi0mM0O3SfON0s/Wv0D2v/9BwMnZulJcwubnWyElTM0KcRvli8Sm9wZr37AtC2mu3sEywPyOt3S3U+RHDiLXhJG/tGU2z1JD27ktQ/4bPLVO8H40zo49HsIw3kdwYZPysUJfoEGviWUG+GbIQFvR0Wy673vv1xj2OLr3QIdJ/WrvfhpEEQuTIqNhn0JkPAyuAh&cdntag=bWFyaz1vc193ZWIscXVhbGl0eV9leGhpZ2g', 'https://p3.music.126.net/02ABQUXRkPKwlS0Hv2xWXA==/901599534776686.jpg?param=300y300', '\r\nhttps://m801.music.126.net/20250905195821/895fb2db9d1c073e8b4d7a7e49dc7a00/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/45693210730/af8d/7664/442c/b54e4319b99a572bbbb628fb3cb45c1b.mp3?vuutv=Y5RnmiD1bhZzkaZi/J5t+veSu/erLFfPYQnvAanEfiutZUCgspNSe52fSRTndo01GdROeWUbPutkNqgr/H+ukLz1NL/sLpVIXspnQfKOzs7H/6URU76fzax3HcFrbUPXf/O8/VNUwJK4kaH60Eat6+vb0q/+mUIohQxPKXC8S/HdgJYMP3vXVrCaEtCHoszi0mM0O3SfON0s/Wv0D2v/9BwMnZulJcwubnWyElTM0KcRvli8Sm9wZr37AtC2mu3sEywPyOt3S3U+RHDiLXhJG/tGU2z1JD27ktQ/4bPLVO8H40zo49HsIw3kdwYZPysUJfoEGviWUG+GbIQFvR0Wy673vv1xj2OLr3QIdJ/WrvfhpEEQuTIqNhn0JkPAyuAh&cdntag=bWFyaz1vc193ZWIscXVhbGl0eV9leGhpZ2g');
INSERT INTO `sys_music` VALUES (2, '活着', '郝云', '天马行空', '\r\nhttps://m701.music.126.net/20250905202402/43045d653e91817995f7e525988d7212/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/28481701350/2538/43db/1558/7a7f66fa24d434ac05c4972a26beab73.mp3?vuutv=bcgxapdSfyhmVpidpOUPHY7Bfm3WjwSuAIpZDYGq90+z4hKtRzKm7S3Cce5i3Pm9PSiMqrszut0djmPeUXbS5K61Uyq/gD8jsaskupyjvLSy/vspri9CAGwuzA7bxAPQ98J1wXMF7oOacjjjJQdO17JHrsrFV2ki/pI7dHVBw60+1EP14rcRHBYDUl57mmTR9qyG+LlrJL8gYw/VL3h6Z/u/BCxIooZb7er9VGTfHo7C905dREq2mHTqgTD4IoGYe9XQJcfglm0Ts+GoCF56myjorDi+HArNZhXngbeVthygrIg+6PKTykhTL5fKEBgwXClGW/QrFptdSlgjKFTj3EXxvvs/bGzAAr4dc1cFqNNEF96pG6YGSwuA3rEuqdhM&cdntag=bWFyaz1vc193ZWIscXVhbGl0eV9leGhpZ2g', 'http://p2.music.126.net/9A9o8KnCftKoJLmfk7jE-A==/1252343744099790.jpg?param=130y130', 'https://www.toomic.com/article/574636.html');
INSERT INTO `sys_music` VALUES (3, '大城小爱', '王力宏', '大城小爱', 'https://m801.music.126.net/20250905211328/7401d0a7c6acd84f9637806f0f915d05/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/61460776093/94f2/5422/be92/d5f5db843a623066a2bc63a0e6ddf59c.mp3?vuutv=KZrJv8HhTR2ohYcAW9k5NrRb44yqh05jgZ//fZDsV631muRHSPvHCjwbmH7ldp1Mlc9R/dbt9/qPIR/y0e5R0sHcjNyWXbz7gzSP2g4A02wY0sSsNmIq+DIbxm+eIaW6+QpkYLzUhKhTkE/S45f0Vsi4lFoETZ7FPWgwz65C5cpRTMXOm8YWXP8dSA5gNmscJkLQCeYGwVb2xB0bx1RyexYlOTsjEfkZ2ZNY9Zh2kGelaR3A6MUIEY5H7veOp1DFjlLYKESvQ6RgNwPsZ2vGBX8WtmSF/XnRoXg+G/VBkvN3psq1uW94KkRfSLqQFo/fz/bBU7i4lyJ3/a63PYQFT6kLjhYBFGJAHs8Ak5pdYc6a4C/f/C+/GQ3JKyWGoAT4&cdntag=bWFyaz1vc193ZWIscXVhbGl0eV9leGhpZ2g', 'https://p3.music.126.net/N9R7Y_zpgRFNAon6QJxEYw==/109951171526337451.jpg?param=300y300', 'http://localhost:9090/file/8f8875cb20c645999c0a63f55668b186.lrc');
INSERT INTO `sys_music` VALUES (4, '莫愁乡', '亞細亞曠世奇才', '莫愁乡', 'https://m801.music.126.net/20250905213411/0a64a1f28644eca2da527a6830500017/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/60384164355/ab7b/ae80/98f7/090c52dbb8546b5e4de1da0c1e6106b4.mp3?vuutv=z5KtUdD3IAXMc8CmXifYgv3ms0Eb0pHoXKkewgPH/jP4oupDgZIHO959Tv/Z7U1j6eM0GBrCmrwO2i0kkOg2aQc19Yp7sFdPqKnfuT7Cjay8uNGaUKssU0l5KBQOX406NhptwLRlavvSAgk1Zq9WNHltYY4bFCzwhJyUB/NXWGn1JRwpXsazSHJCNf5jwWdRy9LHQLwb1hdCURArdIRpa9RW2Y7PpUFM78/rjJ4LXLc8YOVdwtA7kHpglx5BIrVZ4E29pMyh7nU4a1ka3pBb0gHTb2pqgTco0EG0zZnLgk64V4mtkc6NfFO55rqkfjfDHQQyvTIL3AZ2vU5Apk//OxeK6ZFl971/KnF+OEDjvOmVQh+LhsjjBe5PuytT1BwZ&cdntag=bWFyaz1vc193ZWIscXVhbGl0eV9leGhpZ2g', 'https://p3.music.126.net/DVi9B70oYVFtDxVqZf_56g==/109951171264259694.jpg?param=300y300', '');
INSERT INTO `sys_music` VALUES (5, '青花瓷', '刘芳', '再醉一次', 'https://m801.music.126.net/20250905213731/ebdcbce1a82d72ce59a3b6790bde6828/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/34604963228/603c/326b/a230/f66b86ba5d9f090b8c06e5adb76e4eb3.mp3?vuutv=RpEqAhMNcvbh2Qmuw6wv16X/TID4f1rODC7hvGrA3E9yf1mH6lfnQ5kcKhjtfLT/J4rYTvsxc+cE/iEKAtk7OnvhaRDrWzRKB2La6nyV4C7qNo8evxk0+8CBKdqQ2oYXarNyDT/rJVWuWVMHDkru1tdgGZYxJrjmoDuajC1/aig16a7Jfmk448dHgxr6l6Lkr3OxCNZlpLkCemvwK01aegeg77sUS0MojFqeE0turyDjyy8q6L13s/qR25DEhFyKTCqzdJtSlegR+iofX5LTv+4pVH21slsJKziqTEa/W9txoEuyxbQxwKcco56AaA0o5cYCLqMwUfw0beJ7dyG4hG1JIRJKyIVvGKwd/Xxnrfid5fE6YHz00TbNACLS5YJE&cdntag=bWFyaz1vc193ZWIscXVhbGl0eV9leGhpZ2g', 'https://p3.music.126.net/cLuoxPEM6RnJ41i1MyuQqQ==/555253372038862.jpg?param=300y300', '');

-- ----------------------------
-- Table structure for sys_note
-- ----------------------------
DROP TABLE IF EXISTS `sys_note`;
CREATE TABLE `sys_note`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `picture` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
  `information` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  `badge` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `pageview` int NULL DEFAULT NULL COMMENT '浏览量',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_note
-- ----------------------------
INSERT INTO `sys_note` VALUES (1, '12313214444', 'http://localhost:9090/file/e06d8e900ba744a89838d12d2788f8de.jpg', '12312344\n# 1123', 'POTATO', '测试', 2, '2025-09-02 21:34:37');
INSERT INTO `sys_note` VALUES (2, '学习了吗？怎么学习', 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/625526fe452009fb7a5feb8c_new.jpg?imageMogr2/thumbnail/450x300', '# 今天你学习了吗\n\n## 今天要学习啥\n\n*今天把该做的事做了  hhhh*\n**今天基本都完成了**', 'POTATO', '学习啊', 1, '2025-09-03 10:54:02');

-- ----------------------------
-- Table structure for sys_travel
-- ----------------------------
DROP TABLE IF EXISTS `sys_travel`;
CREATE TABLE `sys_travel`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `astyle` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签颜色',
  `imageList` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片列表',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_travel
-- ----------------------------
INSERT INTO `sys_travel` VALUES (22, '兄弟啊抱一下', 'rgb(64, 255, 128)', NULL);
INSERT INTO `sys_travel` VALUES (23, '壁纸', '#409EFF', NULL);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, '2474921543', '123456', '管理员', '2025-08-17 11:24:57');

-- ----------------------------
-- Table structure for travel_image
-- ----------------------------
DROP TABLE IF EXISTS `travel_image`;
CREATE TABLE `travel_image`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `travel_id` int NULL DEFAULT NULL COMMENT '对应sys_travel中的id',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
  `information` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `sort` int NULL DEFAULT NULL COMMENT '同一个id的图片排列顺序',
  `publish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of travel_image
-- ----------------------------
INSERT INTO `travel_image` VALUES (35, 22, 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/625526ff452009fb7a5febb4.jpg?imageMogr2/thumbnail/450x300', '女巫啊', 1, '2025-09-02 16:00:00');
INSERT INTO `travel_image` VALUES (36, 22, 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/625526ff452009fb7a5febb0.jpg?imageMogr2/thumbnail/450x300', '金克斯', 2, '2025-09-02 16:00:00');
INSERT INTO `travel_image` VALUES (37, 22, 'https://cdn.yuanshikong.net/NewTab/wallpaper/video/62552700452009fb7a5febe2.jpg?imageMogr2/thumbnail/450x300', '阳光草地', 3, '2025-09-02 16:00:00');
INSERT INTO `travel_image` VALUES (38, 23, 'https://cdn.yuanshikong.net/NewTab/wallpaper/itab/645324af62269728166a721e.jpe?imageMogr2/thumbnail/450x300', '我喜欢这个', 1, '2025-09-05 00:00:00');
INSERT INTO `travel_image` VALUES (39, 23, 'https://cdn.yuanshikong.net/NewTab/wallpaper/itab/645324c962269728166a7246.jpe?imageMogr2/thumbnail/450x300', '', 2, '2025-09-05 00:00:00');

SET FOREIGN_KEY_CHECKS = 1;

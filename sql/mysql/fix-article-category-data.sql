-- ============================================
-- 文章分类数据修复脚本
-- ============================================
-- 用途：
--   1. 检查现有文章的 category_id 是否为空
--   2. 为没有分类的文章设置默认分类
--   3. 验证数据完整性
-- ============================================

-- 1. 检查分类表数据
SELECT '=== 1. 检查分类表数据 ===' AS '';
SELECT 
    id AS '分类ID',
    name AS '分类名称',
    sort AS '排序',
    create_time AS '创建时间'
FROM emojump_article_category
WHERE deleted = 0
ORDER BY sort ASC;

-- 2. 检查文章的分类分布
SELECT '=== 2. 检查文章的分类分布 ===' AS '';
SELECT 
    IFNULL(category_id, 'NULL') AS '分类ID',
    COUNT(*) AS '文章数量'
FROM emojump_article
WHERE deleted = 0
GROUP BY category_id
ORDER BY category_id;

-- 3. 查看没有分类的文章
SELECT '=== 3. 查看没有分类的文章 ===' AS '';
SELECT 
    id AS '文章ID',
    title AS '文章标题',
    category_id AS '分类ID',
    status AS '状态',
    create_time AS '创建时间'
FROM emojump_article
WHERE deleted = 0 
  AND category_id IS NULL
LIMIT 10;

-- 4. 统计没有分类的文章数量
SELECT '=== 4. 统计没有分类的文章数量 ===' AS '';
SELECT 
    COUNT(*) AS '未设置分类的文章数'
FROM emojump_article
WHERE deleted = 0 
  AND category_id IS NULL;

-- 5. 查看使用了不存在分类的文章
SELECT '=== 5. 查看使用了不存在分类的文章 ===' AS '';
SELECT 
    a.id AS '文章ID',
    a.title AS '文章标题',
    a.category_id AS '分类ID（不存在）',
    a.status AS '状态'
FROM emojump_article a
WHERE a.deleted = 0 
  AND a.category_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 
    FROM emojump_article_category c 
    WHERE c.id = a.category_id 
      AND c.deleted = 0
  )
LIMIT 10;

-- ============================================
-- 修复操作（按需执行）
-- ============================================

-- 方案 1: 为所有没有分类的文章设置第一个分类
-- 注意：执行前请先运行上面的检查语句，确认第一个分类存在

-- 获取第一个分类的 ID（用于下面的 UPDATE）
SELECT '=== 获取第一个分类的 ID ===' AS '';
SELECT 
    id AS '第一个分类ID',
    name AS '分类名称'
FROM emojump_article_category
WHERE deleted = 0
ORDER BY sort ASC, id ASC
LIMIT 1;

-- 执行更新（将 999 替换为上面查询到的分类 ID）
-- UPDATE emojump_article 
-- SET category_id = 999,
--     updater = 'system',
--     update_time = NOW()
-- WHERE deleted = 0 
--   AND category_id IS NULL;

-- 方案 2: 为没有分类的文章创建一个"未分类"分类
-- 注意：先检查是否已存在"未分类"分类

-- 插入"未分类"分类（如果不存在）
-- INSERT INTO emojump_article_category (name, sort, creator, create_time, updater, update_time, deleted, tenant_id)
-- SELECT '未分类', 999, 'system', NOW(), 'system', NOW(), 0, tenant_id
-- FROM emojump_article
-- WHERE NOT EXISTS (
--     SELECT 1 FROM emojump_article_category WHERE name = '未分类' AND deleted = 0
-- )
-- LIMIT 1;

-- 将没有分类的文章设置为"未分类"
-- UPDATE emojump_article a
-- SET a.category_id = (
--     SELECT id 
--     FROM emojump_article_category 
--     WHERE name = '未分类' AND deleted = 0
--     LIMIT 1
-- ),
-- a.updater = 'system',
-- a.update_time = NOW()
-- WHERE a.deleted = 0 
--   AND a.category_id IS NULL;

-- ============================================
-- 验证修复结果
-- ============================================

-- 验证 1: 检查是否还有没有分类的文章
SELECT '=== 验证 1: 检查是否还有没有分类的文章 ===' AS '';
SELECT 
    COUNT(*) AS '未设置分类的文章数（应该为0）'
FROM emojump_article
WHERE deleted = 0 
  AND category_id IS NULL;

-- 验证 2: 检查所有文章的分类是否都存在
SELECT '=== 验证 2: 检查所有文章的分类是否都存在 ===' AS '';
SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✅ 所有文章的分类都存在'
        ELSE CONCAT('❌ 有 ', COUNT(*), ' 篇文章的分类不存在')
    END AS '验证结果'
FROM emojump_article a
WHERE a.deleted = 0 
  AND a.category_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 
    FROM emojump_article_category c 
    WHERE c.id = a.category_id 
      AND c.deleted = 0
  );

-- 验证 3: 查看最终的分类分布
SELECT '=== 验证 3: 最终的分类分布 ===' AS '';
SELECT 
    c.name AS '分类名称',
    c.id AS '分类ID',
    COUNT(a.id) AS '文章数量'
FROM emojump_article_category c
LEFT JOIN emojump_article a ON c.id = a.category_id AND a.deleted = 0
WHERE c.deleted = 0
GROUP BY c.id, c.name
ORDER BY c.sort ASC;

-- ============================================
-- 快速修复命令（推荐）
-- ============================================

-- 如果你确认要使用第一个分类作为默认分类，执行以下操作：

-- 步骤 1: 查看第一个分类
-- SELECT id, name FROM emojump_article_category WHERE deleted = 0 ORDER BY sort ASC LIMIT 1;

-- 步骤 2: 更新所有没有分类的文章（将 1 替换为步骤1查到的分类ID）
-- UPDATE emojump_article 
-- SET category_id = 1, updater = 'system', update_time = NOW()
-- WHERE deleted = 0 AND category_id IS NULL;

-- 步骤 3: 验证
-- SELECT COUNT(*) FROM emojump_article WHERE deleted = 0 AND category_id IS NULL;
-- （应该返回 0）

-- ============================================
-- 示例：创建测试数据
-- ============================================

-- 如果你的数据库中没有测试数据，可以执行以下 SQL：

-- 插入测试分类
-- INSERT INTO emojump_article_category (name, sort, creator, create_time, updater, update_time, deleted, tenant_id)
-- VALUES 
-- ('心理学', 1, 'admin', NOW(), 'admin', NOW(), 0, 1),
-- ('育儿知识', 2, 'admin', NOW(), 'admin', NOW(), 0, 1),
-- ('健康饮食', 3, 'admin', NOW(), 'admin', NOW(), 0, 1),
-- ('亲子游戏', 4, 'admin', NOW(), 'admin', NOW(), 0, 1),
-- ('早期教育', 5, 'admin', NOW(), 'admin', NOW(), 0, 1);

-- 插入测试文章
-- INSERT INTO emojump_article (title, content, cover_image, category_id, status, remark, publish_time, view_count, like_count, creator, create_time, updater, update_time, deleted, tenant_id)
-- VALUES 
-- ('儿童心理发展的5个关键阶段', '<p>文章内容...</p>', 'https://via.placeholder.com/400x300', 1, 1, '了解儿童心理发展', UNIX_TIMESTAMP() * 1000, 0, 0, 'admin', NOW(), 'admin', NOW(), 0, 1),
-- ('0-3岁宝宝护理完全指南', '<p>文章内容...</p>', 'https://via.placeholder.com/400x300', 2, 1, '新手父母必看', UNIX_TIMESTAMP() * 1000, 0, 0, 'admin', NOW(), 'admin', NOW(), 0, 1),
-- ('宝宝营养餐30例', '<p>文章内容...</p>', 'https://via.placeholder.com/400x300', 3, 1, '健康美味的宝宝餐', UNIX_TIMESTAMP() * 1000, 0, 0, 'admin', NOW(), 'admin', NOW(), 0, 1),
-- ('10个亲子互动游戏推荐', '<p>文章内容...</p>', 'https://via.placeholder.com/400x300', 4, 1, '增进亲子关系', UNIX_TIMESTAMP() * 1000, 0, 0, 'admin', NOW(), 'admin', NOW(), 0, 1),
-- ('蒙特梭利教育理念', '<p>文章内容...</p>', 'https://via.placeholder.com/400x300', 5, 1, '早教方法介绍', UNIX_TIMESTAMP() * 1000, 0, 0, 'admin', NOW(), 'admin', NOW(), 0, 1);







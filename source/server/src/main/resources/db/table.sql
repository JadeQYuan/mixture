
-- 用户信息表
-- 存储系统用户的基本信息，包括账号、密码、角色、人脸特征等
CREATE TABLE user_info (
    id INTEGER PRIMARY KEY,                    -- 用户ID，主键，自增
    user_name text,                           -- 用户姓名
    account text,                             -- 登录账号
    password text,                            -- 登录密码
    role_code text,                           -- 用户角色代码（admin-管理员，user-普通用户）
    face_path text,                           -- 人脸图片路径
    face_feature text,                        -- 人脸特征数据（用于人脸识别）
    remark text,                              -- 备注信息
    del_flag INTEGER,                         -- 删除标识 0 删除，1 未删除
    create_time text,                         -- 创建时间
    update_time text                          -- 更新时间
);

-- 料罐信息表
-- 存储料罐的基本信息，包括料罐编号、所属用户等
CREATE TABLE tank_info (
    id INTEGER PRIMARY KEY,                    -- 料罐ID，主键，自增
    tank_no text,                             -- 料罐编号（唯一标识）
    remark text,                              -- 备注信息
    user_id INTEGER,                          -- 所属用户ID（关联user_info表）
    del_flag INTEGER,                         -- 删除标识 0 删除，1 未删除
    create_time text,                         -- 创建时间
    update_time text                          -- 更新时间
);

-- 混合料信息表
-- 存储加料操作的详细信息，包括申请、加料、退料的完整流程记录
CREATE TABLE mixture_info (
    id INTEGER PRIMARY KEY,                    -- 加料记录ID，主键，自增
    tank_id INTEGER,                          -- 料罐ID（关联tank_info表）
    tank_no text,                             -- 料罐编号
    shift_type text,                          -- 班次类型（day-白班，night-夜班）
    material_name text,                       -- 材料名称（10KV、35KV等）
    product_spec text,                        -- 产品规格型号
    plan_weight real,                         -- 计划加料重量（kg）
    apply_user_id INTEGER,                     -- 申请用户ID（关联user_info表）
    apply_time text,                          -- 申请加料时间
    bottom_weight real,                       -- 罐底重量（kg）
    full_weight real,                         -- 满罐重量（kg）
    flame_retardant_weight real,              -- 阻燃粉重量（kg）
    feeding_time text,                        -- 实际加料时间
    feeding_user_id INTEGER,                  -- 加料操作员ID（关联user_info表）
    return_weight real,                       -- 退料重量（kg）
    return_time text,                         -- 退料时间
    actual_weight real,                       -- 实际用料重量（kg）
    remark text,                              -- 备注信息
    status INTEGER,                            -- 状态（1-待处理，2-已处理，3-其他）
    create_time text,                         -- 创建时间
    update_time text                          -- 更新时间
);

ALTER TABLE mixture_info ADD COLUMN picking_time text after feeding_user_id;
ALTER TABLE mixture_info ADD COLUMN picking_user_id INTEGER after picking_time;
ALTER TABLE mixture_info ADD COLUMN picking_bottom_weight real;
ALTER TABLE mixture_info ADD COLUMN picking_total_weight real;
ALTER TABLE mixture_info ADD COLUMN flame_retardant_abnormal INTEGER DEFAULT 0;

-- 校验信息表
CREATE TABLE check_info (
    id INTEGER PRIMARY KEY,                   -- 加料记录ID，主键，自增
    tank_id INTEGER,                          -- 料罐ID（关联tank_info表）
    tank_no text,                             -- 料罐编号
    return_id INTEGER,                        -- 退料记录
    return_weight real,                       -- 退料重量（kg）
    bottom_id INTEGER,                        -- 加料记录
    bottom_weight real,                       -- 罐底重量（kg）
    opinion text,                             -- 物料员意见
    user_id INTEGER,                          -- 物料员ID
    admin_opinion text,                       -- 管理员意见
    admin_id INTEGER,                         -- 管理员ID
    status INTEGER,                           -- 状态
    correct_weight real,                      -- 校正重量（kg）
    create_time text,                         -- 创建时间
    update_time text                          -- 更新时间
);

--人脸信息表
CREATE TABLE face_info (
                           id INTEGER PRIMARY KEY,
                           user_id text,
                           user_name text,
                           face_path text,
                           face_feature text,
                           create_time text,
                           update_time text
);

--用户信息表
CREATE TABLE user_info (
                           id INTEGER PRIMARY KEY,
                           user_id text,
                           user_name text,
                           account text,
                           password text,
                           role_code text,
                           remark text,
                           create_time text,
                           update_time text
);

--料罐表
CREATE TABLE bucket_info (
                             id INTEGER PRIMARY KEY,
                             user_id text,
                             bucket_no text,
                             capacity real,
                             abs real,
                             remark text,
                             create_time text,
                             update_time text
);

--料罐表申请表,type:add 加料，del 退料; status:0 未处理，1已处理
CREATE TABLE bucket_apply_log (
                                  id INTEGER PRIMARY KEY,
                                  user_id text,
                                  bucket_no text,
                                  spec text,
                                  capacity real,
                                  type text,
                                  status text,
                                  remark text,
                                  create_time text,
                                  update_time text
);

--加料操作记录表
CREATE TABLE bucket_operate_log (
                                    id INTEGER PRIMARY KEY,
                                    user_id text,
                                    bucket_no text,
                                    capacity real,
                                    capacity_add real,
                                    abs real,
                                    remark text,
                                    create_time text,
                                    update_time text
);
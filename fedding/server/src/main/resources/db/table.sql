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
                           role_id text,
                           role_name text,
                           remark text,
                           create_time text,
                           update_time text
);

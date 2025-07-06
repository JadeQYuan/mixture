package com.tee.mapper;

import com.tee.pojo.vo.Face;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FaceMapper {

    List<Face> getFaceInfo(@Param("userId") String userId);

    void insertFaceInfo(Face face);

    void updateFaceInfo(Face face);
}

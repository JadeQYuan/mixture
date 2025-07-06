package com.tee.service;

import com.tee.mapper.FaceMapper;
import com.tee.pojo.vo.Face;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FaceService {

    @Autowired
    FaceMapper faceMapper;

    public List<Face> getFaceInfo(@Param("userId") String userId) {
        return faceMapper.getFaceInfo(userId);
    }

    public void updateFaceInfo(Face face) {
        faceMapper.updateFaceInfo(face);
    }

    public void insertFaceInfo(Face face) {
        faceMapper.insertFaceInfo(face);
    }
}

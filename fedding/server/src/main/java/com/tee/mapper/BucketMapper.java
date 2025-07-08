package com.tee.mapper;

import com.tee.pojo.qo.BucketQo;
import com.tee.pojo.vo.Bucket;
import com.tee.pojo.vo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BucketMapper {

    List<Bucket> getBucketInfo(BucketQo bucketQo);

    List<Bucket> getBucketLog(BucketQo bucketQo);

    List<Bucket> getBucketApplyLog(BucketQo bucketQo);

    void insertBucketApply(BucketQo user);

    void updateBucketApply(BucketQo user);

    void insertBucket(BucketQo user);

    void updateBucketNum(BucketQo user);

    void updateBucketInfo(BucketQo user);

    void insertBucketOperateLog(BucketQo user);

    void deleteBucket(BucketQo user);



}

package com.tee.service;

import com.tee.mapper.BucketMapper;
import com.tee.pojo.qo.BucketQo;
import com.tee.pojo.vo.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BucketService {

    @Autowired
    BucketMapper bucketMapper;

    public List<Bucket> getBucketInfo(BucketQo bucketQo) {
        return bucketMapper.getBucketInfo(bucketQo);
    }

    public List<Bucket> getBucketLog(BucketQo bucketQo) {
        return bucketMapper.getBucketLog(bucketQo);
    }

    public List<Bucket> getBucketApplyLog(BucketQo bucketQo) {
        return bucketMapper.getBucketApplyLog(bucketQo);
    }

    public void insertBucketApply(BucketQo bucketQo) {
        bucketMapper.insertBucketApply(bucketQo);
    }

    public void updateBucketApply(BucketQo bucketQo) {
        bucketMapper.updateBucketApply(bucketQo);
    }

    public void insertBucket(BucketQo bucketQo) {
        bucketMapper.insertBucket(bucketQo);
    }

    public void updateBucketNum(BucketQo bucketQo) {
        bucketMapper.updateBucketNum(bucketQo);
    }

    public void updateBucketInfo(BucketQo bucketQo) {
        bucketMapper.updateBucketInfo(bucketQo);
    }

    public void insertBucketOperateLog(BucketQo bucketQo) {
        bucketMapper.insertBucketOperateLog(bucketQo);
    }

    public void deleteBucket(BucketQo bucketQo) {
        bucketMapper.deleteBucket(bucketQo);
    }
}

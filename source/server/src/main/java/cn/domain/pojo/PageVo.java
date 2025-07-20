package cn.domain.pojo;

import com.github.pagehelper.Page;
import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {

    private long total;

    private List<T> data;

    public PageVo() {

    }

    public PageVo(Page<T> page) {
        this.total = page.getTotal();
        this.data = page.getResult();
    }
}

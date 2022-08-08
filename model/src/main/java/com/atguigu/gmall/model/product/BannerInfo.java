package com.atguigu.gmall.model.product;

import com.atguigu.gmall.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author dushuo
 * @Date 2022/8/7 12:18
 * @Version 1.0
 */
@Data
@ApiModel(description = "banner列表")
@TableName("banner_info")
public class BannerInfo extends BaseEntity {

    @TableField("title")
    private String title;

    @TableField("banner_url")
    private String bannerUrl;

    @TableField("to_url")
    private String toUrl;

    @TableField("sort")
    private Integer sort;

}

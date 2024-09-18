package com.shuaigef.springbootinit.common.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    @ApiModelProperty(value = "起始页", example = "1")
    private long current = 1;

    /**
     * 页面大小
     */
    @ApiModelProperty(value = "每页记录数", example = "10")
    private long pageSize = 10;
}

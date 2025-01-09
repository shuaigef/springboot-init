package com.shuaigef.springbootinit.common.request;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * 批量删除请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Data
public class DeleteBatchRequest implements Serializable {

    /**
     * ids
     */
    @NotNull(message = "ids 不能为空")
    @Size(min = 1, message = "ids 长度必须大于 0")
    private List<Long> ids;

    private static final long serialVersionUID = 1L;
}

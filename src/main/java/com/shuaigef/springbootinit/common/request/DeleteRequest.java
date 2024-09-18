package com.shuaigef.springbootinit.common.request;

import java.io.Serializable;
import javax.validation.constraints.Min;
import lombok.Data;

/**
 * 删除请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    @Min(value = 1, message = "id 必须大于 0")
    private Long id;

    private static final long serialVersionUID = 1L;
}

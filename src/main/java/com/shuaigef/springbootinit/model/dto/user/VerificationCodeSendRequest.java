package com.shuaigef.springbootinit.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 验证码发送请求
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ApiModel(description = "验证码发送请求")
@Data
public class VerificationCodeSendRequest implements Serializable {

    /**
     * 验证码发送对象
     */
    @ApiModelProperty(value = "验证码发送对象")
    @NotBlank(message = "验证码发送对象不能为空")
    private String target;

    /**
     * 验证码业务类型
     */
    @ApiModelProperty(value = "验证码业务类型")
    @NotBlank(message = "biz 不能为空")
    private String biz;

    private static final long serialVersionUID = 1L;

}

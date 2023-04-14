package com.minibyte.bo.dto.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * kv存储信息表
 * </p>
 *
 * @author MiniByte
 * @since 2021-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("KV_INFO")
public class KvInfo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "KEY", type = IdType.AUTO)
    private String key;

    @TableField("VALUE")
    private String value;

    @TableField("COMMENT")
    private String comment;


}

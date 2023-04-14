package com.minibyte.bo.pojo.app;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: MiniByte
 * @date: 2021-09-15
 * @description: yaml验证比较工具配置表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("YCC_CONFIG")
public class YccConfig implements Serializable {
    private static final long serialVersionUID=1L;

    // 主键
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    // 配置名称
    @TableField("NAME")
    private String name;

    // 配置分组
    @TableField("GROUP_NAME")
    private String groupName;

    // 配置类型 1.本地文件 2.nacos配置
    @TableField("CONFIG_TYPE")
    private Integer configType;

    // 本地文件路径
    @TableField("LOCAL_FILE_PATH")
    private String localFilePath;

    // nacos服务地址 eg: http://127.0.0.1:30200
    @TableField("NACOS_SERVER_ADDR")
    private String nacosServerAddr;

    // nacos配置项data-id
    @TableField("NACOS_DATA_ID")
    private String nacosDataId;

    // nacos配置项group
    @TableField("NACOS_GROUP")
    private String nacosGroup;
}

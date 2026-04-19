package com.eims.common.core.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类，所有业务表对应的实体继承此类
 */
@Data
public class BaseEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId = 1L;

    /** 逻辑删除标记：0-正常，1-已删除 */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted = 0;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 创建人 */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser = 0L;

    /** 更新人 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser = 0L;
}

package com.huike.video.common.entity; 
 
import com.baomidou.mybatisplus.annotation.FieldFill; 
import com.baomidou.mybatisplus.annotation.TableField; 
import lombok.Data; 
import java.io.Serializable; 
import java.time.LocalDateTime; 
 
@Data 
public class BaseEntity implements Serializable { 
 
    private static final long serialVersionUID = 1L; 
 
    @TableField(fill = FieldFill.INSERT) 
    private LocalDateTime createTime; 
 
    @TableField(fill = FieldFill.INSERT_UPDATE) 
    private LocalDateTime updateTime; 
 
    @TableField(fill = FieldFill.INSERT) 
    private String createBy; 
 
    @TableField(fill = FieldFill.INSERT_UPDATE) 
    private String updateBy; 
 
    private Integer deleted; 
 
    public LocalDateTime getCreatedAt() { return this.createTime; } 
 
    public LocalDateTime getUpdatedAt() { return this.updateTime; } 
} 

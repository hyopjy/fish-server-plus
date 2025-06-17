package fish.plus.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("GroupUser")
public class GroupUserEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField(value = "groupId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    @TableField(value = "userId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @TableField(value = "userNick")
    private String userNick;
}

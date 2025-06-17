package fish.plus.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("GroupInfo")
public class GroupInfoEntity {

    @TableId(value = "groupId", type = IdType.INPUT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;


    @TableField(value = "groupName")
    private String groupName;
}

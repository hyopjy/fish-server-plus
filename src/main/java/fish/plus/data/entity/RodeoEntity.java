package fish.plus.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("Rodeo")  // 对应数据库表名
public class RodeoEntity {
    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    // 玩法（决斗、轮盘、大乱斗）
    @TableField(value = "playingMethod")
    private String playingMethod;

    // 群组id
    @TableField(value = "groupId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    @TableField(value = "venue")
    private String venue;
    // 配置日期   2024-08-23

    @TableField(value = "day")
    private String day;

    // 时间段  10:15:00
    @TableField(value = "startTime")
    private String startTime;

    // 时间段
    @TableField(value = "endTime")
    private String endTime;

    @TableField(value = "players")
    private String players;

    // 局数
    @TableField(value = "round")
    private int round;

    @TableField(value = "running")
    private int running;


}

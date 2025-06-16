package fish.plus.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("RodeoRecord")
public class RodeoRecordEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场次id
     */
    @TableField(value = "rodeoId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long rodeoId;

    /**
     * 玩家
     */
    @TableField(value = "player")
    private String player;

    /**
     * 禁言时长
     */
    @TableField(value = "ForbiddenSpeech")
    private Integer ForbiddenSpeech;

    /**
     * 第几局
     */
    @TableField(value = "turns")
    private Integer turns;

    /**
     * 比赛描述
     */
    @TableField(value = "rodeoDesc")
    private String rodeoDesc;

    @TableField(value = "winFlag")
    private int winFlag;


}

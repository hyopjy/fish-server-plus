package fish.plus.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("RodeoRecord")
public class RodeoRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 场次id
     */
    private Long rodeoId;

    /**
     * 玩家
     */
    private String player;

    /**
     * 禁言时长
     */
    private Integer ForbiddenSpeech;

    /**
     * 第几局
     */
    private Integer turns;

    /**
     * 比赛描述
     */
    private String rodeoDesc;

    private int winFlag;
}

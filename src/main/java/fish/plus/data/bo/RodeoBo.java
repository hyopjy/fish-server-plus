package fish.plus.data.bo;

import lombok.Data;

@Data
public class RodeoBo {

    // 玩法（决斗、轮盘、大乱斗）
    private String playingMethod;

    // 群组id
    private Long groupId;

    private String venue;
    // 配置日期   2024-08-23
    private String day;

    // 时间段  10:15:00
    private String startTime;

    // 时间段
    private String endTime;

    private String players;

    // 局数
    private int round;
}

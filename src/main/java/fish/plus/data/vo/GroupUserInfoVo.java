package fish.plus.data.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class GroupUserInfoVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    private String groupName;

    private List<GroupUserInfo> groupUserList;

    @Data
    public static class GroupUserInfo {
        @JsonSerialize(using = ToStringSerializer.class)
        private Long userId;

        private String userNick;
    }

}

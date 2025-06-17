package fish.plus.data.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import fish.plus.data.entity.GroupInfoEntity;
import fish.plus.data.entity.GroupUserEntity;
import lombok.Data;

import java.util.List;

@Data
public class GroupUserInfoVo {

    private GroupInfoEntity group;

    private List<GroupUserEntity> groupUserList;


}

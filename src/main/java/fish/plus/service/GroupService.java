package fish.plus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fish.plus.data.entity.GroupInfoEntity;
import fish.plus.data.entity.GroupUserEntity;
import fish.plus.data.vo.GroupUserInfoVo;
import fish.plus.data.vo.Result;
import fish.plus.mapper.GroupInfoMapper;
import fish.plus.mapper.GroupUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    private GroupInfoMapper groupInfoMapper;

    @Autowired
    private GroupUserMapper groupUserMapper;


    public Result<List<GroupUserInfoVo>> getGroupUser() {
        List<GroupInfoEntity> groupInfoEntities = groupInfoMapper.selectList(new LambdaQueryWrapper<>());
        List<GroupUserEntity> groupUserEntityList = groupUserMapper.selectList(new LambdaQueryWrapper<>());
        Map<Long, List<GroupUserEntity>> groupUserMap = groupUserEntityList.stream().collect(Collectors.groupingBy(GroupUserEntity::getGroupId));

        List<GroupUserInfoVo> voList = new ArrayList<>();
        groupInfoEntities.forEach(group->{
            GroupUserInfoVo vo = new GroupUserInfoVo();
            vo.setGroup(group);

            List<GroupUserEntity> userEntities = Optional.ofNullable(groupUserMap.get(group.getGroupId())).orElse(new ArrayList<>());
            vo.setGroupUserList(userEntities);
            voList.add(vo);
        });
        return Result.ok(voList);

    }
}

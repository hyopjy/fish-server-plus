package fish.plus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fish.plus.config.BusinessException;
import fish.plus.data.bo.RodeoBo;
import fish.plus.data.entity.RodeoEntity;
import fish.plus.data.entity.RodeoRecordEntity;
import fish.plus.data.vo.RodeoInfoVo;
import fish.plus.mapper.RodeoMapper;
import fish.plus.mapper.RodeoRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class RodeoService {

    @Autowired
    private RodeoMapper rodeoMapper;

    @Autowired
    private RodeoRecordMapper rodeoRecordMapper;

    @Autowired
    private MqttNotificationService mqttNotificationService;


    public RodeoInfoVo getRodeoInfoVo(Long groupId) {
        LambdaQueryWrapper<RodeoEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RodeoEntity::getGroupId, groupId);
        lqw.last("limit 1");
        RodeoEntity rodeo  = rodeoMapper.selectOne(lqw);
        if(Objects.isNull(rodeo)){
            return  null;
        }
        LambdaQueryWrapper<RodeoRecordEntity> recordLqw = new LambdaQueryWrapper<>();
        recordLqw.eq(RodeoRecordEntity::getRodeoId, rodeo.getId());
        List<RodeoRecordEntity> recordList = rodeoRecordMapper.selectList(recordLqw);

        RodeoInfoVo vo = new RodeoInfoVo();
        vo.setRodeo(rodeo);
        vo.setRecordList(recordList);
        return vo;
    }

    public void addRodeo(RodeoBo rodeoBo) {
        LambdaQueryWrapper<RodeoEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RodeoEntity::getGroupId, rodeoBo.getGroupId());
        lqw.last("limit 1");
        RodeoEntity rodeo  = rodeoMapper.selectOne(lqw);
        // BusinessException
        if(Objects.nonNull(rodeo) && 1 == rodeo.getRunning()){
            throw new BusinessException("游戏正在进行,不允许创建");
        }
        RodeoEntity saveRodeo = new RodeoEntity();
        if (Objects.nonNull(rodeo)) {
            saveRodeo.setId(rodeo.getId());
        }
        saveRodeo.setPlayingMethod(rodeoBo.getPlayingMethod());
        saveRodeo.setGroupId(rodeoBo.getGroupId());
        saveRodeo.setVenue(rodeoBo.getVenue());
        saveRodeo.setDay(rodeoBo.getDay());
        saveRodeo.setStartTime(rodeoBo.getStartTime() + ":00");
        saveRodeo.setEndTime(rodeoBo.getEndTime() + ":00");
        saveRodeo.setPlayers(rodeoBo.getPlayers());
        saveRodeo.setRound(rodeoBo.getRound());
        saveRodeo.setRunning(0);
        saveRodeo.setGiveProp(rodeoBo.getGiveProp());
        saveRodeo.setPropCode(rodeoBo.getPropCode());
        saveRodeo.setPropName(rodeoBo.getPropName());
        rodeoMapper.insertOrUpdate(saveRodeo);

    }

    public void openGame(Long groupId) {
        LambdaQueryWrapper<RodeoEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RodeoEntity::getGroupId, groupId);
        lqw.last("limit 1");
        RodeoEntity rodeo  = rodeoMapper.selectOne(lqw);
        // BusinessException
        if(Objects.nonNull(rodeo) && 1 == rodeo.getRunning()){
            throw new BusinessException("游戏正在进行,不允许开始");
        }

        // todo 发送消息
        mqttNotificationService.sendGroupRodeoMessage(rodeo.getGroupId());
        rodeo.setRunning(1);
        rodeoMapper.updateById(rodeo);

    }


    public List<RodeoRecordEntity> getRecordList(Long rodeoId) {
        LambdaQueryWrapper<RodeoRecordEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RodeoRecordEntity::getRodeoId, rodeoId);
        return rodeoRecordMapper.selectList(lqw);
    }

    public void stopGame(Long groupId) {
    }
}

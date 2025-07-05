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


    public RodeoInfoVo getRodeoInfoVo(Long rodeoId) {
        LambdaQueryWrapper<RodeoEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RodeoEntity::getId, rodeoId);
        lqw.last("limit 1");
        RodeoEntity rodeo  = rodeoMapper.selectOne(lqw);
        if(Objects.isNull(rodeo)){
            return  null;
        }
        LambdaQueryWrapper<RodeoRecordEntity> recordLqw = new LambdaQueryWrapper<>();
        recordLqw.eq(RodeoRecordEntity::getRodeoId, rodeo.getId());
        List<RodeoRecordEntity> recordList = rodeoRecordMapper.selectList(recordLqw);

        RodeoInfoVo vo = new RodeoInfoVo();
        rodeo.setPlayers(getOutOfPlayers(rodeo.getPlayers()));
        vo.setRodeo(rodeo);
        vo.setRecordList(recordList);
        return vo;
    }

    public void addRodeo(RodeoBo rodeoBo) {
        String players = getPlayers(rodeoBo.getPlayers());

        RodeoEntity saveRodeo = new RodeoEntity();
        saveRodeo.setPlayingMethod(rodeoBo.getPlayingMethod());
        saveRodeo.setGroupId(rodeoBo.getGroupId());
        saveRodeo.setVenue(rodeoBo.getVenue());
        saveRodeo.setDay(rodeoBo.getDay());
        saveRodeo.setStartTime(rodeoBo.getStartTime() + ":00");
        saveRodeo.setEndTime(rodeoBo.getEndTime() + ":00");
        saveRodeo.setPlayers(players);
        saveRodeo.setRound(rodeoBo.getRound());
        saveRodeo.setRunning(0);
        saveRodeo.setGiveProp(rodeoBo.getGiveProp());
        saveRodeo.setPropCode(rodeoBo.getPropCode());
        saveRodeo.setPropName(rodeoBo.getPropName());
        rodeoMapper.insertOrUpdate(saveRodeo);

    }

    public String getPlayers(String players) {
        // 避免添加多余的逗号
        if (players == null || players.isEmpty()) {
           return  "";
        } else {
            // 确保只在需要时添加逗号
            if (!players.startsWith(",") && !players.endsWith(",")) {
                return "," + players + ",";
            } else if (!players.startsWith(",")) {
                return "," + players;
            } else if (!players.endsWith(",")) {
                return players + ",";
            } else {
                return players;
            }
        }
    }

    public String getOutOfPlayers(String players) {
        // 如果players为空，直接返回
        if (players == null || players.isEmpty()) {
            return "";
        }

        // 去掉开头和结尾的逗号
        if (players.startsWith(",") && players.endsWith(",")) {
            return players.substring(1, players.length() - 1);
        }
        // 只去掉开头的逗号
        else if (players.startsWith(",")) {
            return players.substring(1);
        }
        // 只去掉结尾的逗号
        else if (players.endsWith(",")) {
            return players.substring(0, players.length() - 1);
        }
        // 没有逗号的情况
        return players;
    }

    public void openGame(Long rodeoId) {
        LambdaQueryWrapper<RodeoEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RodeoEntity::getId, rodeoId);
        lqw.last("limit 1");
        RodeoEntity rodeo  = rodeoMapper.selectOne(lqw);
        // BusinessException
        if(Objects.nonNull(rodeo) && 1 == rodeo.getRunning()){
            throw new BusinessException("游戏正在进行,不允许开始");
        }

        // todo 发送消息
        mqttNotificationService.sendInitRodeoMessage(rodeo.getId(), rodeo.getGroupId());
        rodeo.setRunning(1);
        rodeoMapper.updateById(rodeo);

    }

    public List<RodeoRecordEntity> getRecordList(Long rodeoId) {
        LambdaQueryWrapper<RodeoRecordEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RodeoRecordEntity::getRodeoId, rodeoId);
        return rodeoRecordMapper.selectList(lqw);
    }

    public void stopGame(Long rodeoId) {
        LambdaQueryWrapper<RodeoEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RodeoEntity::getId, rodeoId);
        lqw.last("limit 1");
        RodeoEntity rodeo  = rodeoMapper.selectOne(lqw);
        if(Objects.isNull(rodeo)){
            throw new BusinessException("数据不存在");
        }
        mqttNotificationService.sendDeleteMessage(rodeo.getId(), rodeo.getGroupId());
    }

    public List<RodeoEntity> getRodeoList(Long groupId) {
        LambdaQueryWrapper<RodeoEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RodeoEntity::getGroupId, groupId);
        List<RodeoEntity> list = rodeoMapper.selectList(lqw);
        list.forEach(rodeo->{
            rodeo.setPlayers(getOutOfPlayers(rodeo.getPlayers()));
        });
        return list;
    }

    public void restartGame(Long rodeoId) {
        LambdaQueryWrapper<RodeoEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RodeoEntity::getId, rodeoId);
        lqw.last("limit 1");
        RodeoEntity rodeo  = rodeoMapper.selectOne(lqw);
        if(Objects.isNull(rodeo)){
            throw new BusinessException("数据不存在");
        }
        mqttNotificationService.sendInitRodeoMessage(rodeo.getId(), rodeo.getGroupId());
    }
}

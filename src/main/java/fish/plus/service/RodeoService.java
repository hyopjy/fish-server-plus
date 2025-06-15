package fish.plus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fish.plus.data.bo.RodeoBo;
import fish.plus.data.entity.RodeoEntity;
import fish.plus.data.entity.RodeoRecordEntity;
import fish.plus.data.vo.RodeoInfoVo;
import fish.plus.mapper.RodeoMapper;
import fish.plus.mapper.RodeoRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RodeoService {

    @Autowired
    private RodeoMapper rodeoMapper;

    @Autowired
    private RodeoRecordMapper rodeoRecordMapper;

    public RodeoInfoVo getRodeoInfoVo() {
        LambdaQueryWrapper<RodeoEntity> lqw = new LambdaQueryWrapper<>();
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

    public Map addRodeo(RodeoBo rodeoBo) {

        return new HashMap<>();
    }
}

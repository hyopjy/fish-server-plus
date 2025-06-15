package fish.plus.data.vo;

import fish.plus.data.entity.RodeoEntity;
import fish.plus.data.entity.RodeoRecordEntity;
import lombok.Data;

import java.util.List;

@Data
public class RodeoInfoVo {

    private RodeoEntity rodeo;

    private List<RodeoRecordEntity> recordList;

}

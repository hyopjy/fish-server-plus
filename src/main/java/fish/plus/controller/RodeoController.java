package fish.plus.controller;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import fish.plus.data.bo.RodeoBo;
import fish.plus.data.entity.RodeoEntity;
import fish.plus.data.entity.RodeoRecordEntity;
import fish.plus.data.vo.GroupUserInfoVo;
import fish.plus.data.vo.Result;
import fish.plus.data.vo.RodeoInfoVo;
import fish.plus.service.GroupService;
import fish.plus.service.RodeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping()
public class RodeoController {

    @Autowired
    private RodeoService rodeoService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/all-group-user")
    public Result<List<GroupUserInfoVo>> getAllGroupUser(){
        return groupService.getGroupUser();
    }

    @GetMapping("/rodeo/query")
    public Result<RodeoInfoVo> getRodeoInfoVo(@RequestParam("rodeoId")
                                                  @JsonSerialize(using = ToStringSerializer.class) Long rodeoId){
        return Result.ok(rodeoService.getRodeoInfoVo(rodeoId));
    }

    @GetMapping("/rodeo/list")
    public Result<List<RodeoEntity>> getRodeoList(@RequestParam("groupId")
                                            @JsonSerialize(using = ToStringSerializer.class) Long groupId){
        return Result.ok(rodeoService.getRodeoList(groupId));
    }


    @PostMapping("/rodeo/add")
    public Result addRodeo(@RequestBody RodeoBo rodeoBo){
        rodeoService.addRodeo(rodeoBo);
        return  Result.ok(null);
    }

    @PostMapping("/rodeo/open-game")
    public Result openGame(@RequestParam("rodeoId")
                               @JsonSerialize(using = ToStringSerializer.class) Long rodeoId){
        rodeoService.openGame(rodeoId);
        return  Result.ok(null);
    }

    @PostMapping("/rodeo/stop-game")
    public Result stopGame(@RequestParam("rodeoId")
                           @JsonSerialize(using = ToStringSerializer.class) Long rodeoId){
        rodeoService.stopGame(rodeoId);
        return  Result.ok(null);
    }

    @PostMapping("/rodeo/restart-game")
    public Result restartGame(@RequestParam("rodeoId")
                           @JsonSerialize(using = ToStringSerializer.class) Long rodeoId){
        rodeoService.restartGame(rodeoId);
        return  Result.ok(null);
    }




    @PostMapping("/record/list")
    public Result<List<RodeoRecordEntity>> getRecordList(@RequestParam("rodeoId")
                                                             @JsonSerialize(using = ToStringSerializer.class)
                                                             Long rodeoId){
        List<RodeoRecordEntity> rodeoRecordEntityList = rodeoService.getRecordList(rodeoId);
        return  Result.ok(rodeoRecordEntityList);
    }


}

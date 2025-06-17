package fish.plus.controller;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import fish.plus.data.bo.RodeoBo;
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
@RequestMapping("/fish-server")
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
    public Result<RodeoInfoVo> getRodeoInfoVo(@RequestParam("groupId")
                                                  @JsonSerialize(using = ToStringSerializer.class) Long groupId){
        return Result.ok(rodeoService.getRodeoInfoVo(groupId));
    }

    @PostMapping("/rodeo/add")
    public Result addRodeo(@RequestBody RodeoBo rodeoBo){
        rodeoService.addRodeo(rodeoBo);
        return  Result.ok(null);
    }

    @PostMapping("/rodeo/open-game")
    public Result openGame(@RequestParam("groupId")
                               @JsonSerialize(using = ToStringSerializer.class) Long groupId){
        rodeoService.openGame(groupId);
        return  Result.ok(null);
    }


}

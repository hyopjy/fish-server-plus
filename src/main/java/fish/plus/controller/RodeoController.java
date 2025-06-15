package fish.plus.controller;


import fish.plus.data.bo.RodeoBo;
import fish.plus.data.vo.RodeoInfoVo;
import fish.plus.service.RodeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("/rodeo")
public class RodeoController {

    @Autowired
    private RodeoService rodeoService;
    @GetMapping("/query")
    public RodeoInfoVo getRodeoInfoVo(){
        return rodeoService.getRodeoInfoVo();
    }

    @GetMapping("/add")
    public Map addRodeo(@RequestBody RodeoBo rodeoBo){
        return rodeoService.addRodeo(rodeoBo);
    }
}

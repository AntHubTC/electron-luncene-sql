package com.minibyte.controller;

import com.minibyte.bo.pojo.app.FileDocDto;
import com.minibyte.bo.pojo.app.SqlSearchDto;
import com.minibyte.common.MBResponse;
import com.minibyte.service.FileIndexSearcherService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("fileIndex")
public class FileIndexController {

    @Resource
    private FileIndexSearcherService fileIndexSearcherService;

    @PostMapping("search")
    @ResponseBody
    public MBResponse<List<FileDocDto>> search(@RequestBody SqlSearchDto sqlSearchDto) {
        List<FileDocDto> list = fileIndexSearcherService.search(sqlSearchDto);
        return MBResponse.ofSuccess(list);
    }
}

package com.hxl.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxl.inventory.model.entity.Drug;
import com.hxl.inventory.service.DrugService;
import com.hxl.inventory.mapper.DrugMapper;
import org.springframework.stereotype.Service;

/**
* @author 29358
* @description 针对表【t_drug(药品表)】的数据库操作Service实现
* @createDate 2026-04-24 23:24:54
*/
@Service
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug>
    implements DrugService{

}





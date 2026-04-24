package com.hxl.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxl.inventory.model.entity.Manufacturer;
import com.hxl.inventory.service.ManufacturerService;
import com.hxl.inventory.mapper.ManufacturerMapper;
import org.springframework.stereotype.Service;

/**
* @author 29358
* @description 针对表【t_manufacturer(生产厂家表)】的数据库操作Service实现
* @createDate 2026-04-24 23:24:54
*/
@Service
public class ManufacturerServiceImpl extends ServiceImpl<ManufacturerMapper, Manufacturer>
    implements ManufacturerService{

}





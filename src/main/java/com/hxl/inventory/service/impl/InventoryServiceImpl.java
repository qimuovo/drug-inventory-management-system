package com.hxl.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxl.inventory.model.entity.Inventory;
import com.hxl.inventory.service.InventoryService;
import com.hxl.inventory.mapper.InventoryMapper;
import org.springframework.stereotype.Service;

/**
* @author 29358
* @description 针对表【t_inventory(库存表)】的数据库操作Service实现
* @createDate 2026-04-24 23:24:54
*/
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory>
    implements InventoryService{

}





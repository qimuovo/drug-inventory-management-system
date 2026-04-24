package com.hxl.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxl.inventory.model.entity.InboundItem;
import com.hxl.inventory.service.InboundItemService;
import com.hxl.inventory.mapper.InboundItemMapper;
import org.springframework.stereotype.Service;

/**
* @author 29358
* @description 针对表【t_inbound_item(入库明细)】的数据库操作Service实现
* @createDate 2026-04-24 23:24:54
*/
@Service
public class InboundItemServiceImpl extends ServiceImpl<InboundItemMapper, InboundItem>
    implements InboundItemService{

}





package com.hxl.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxl.inventory.model.entity.Inbound;
import com.hxl.inventory.service.InboundService;
import com.hxl.inventory.mapper.InboundMapper;
import org.springframework.stereotype.Service;

/**
* @author 29358
* @description 针对表【t_inbound(入库单)】的数据库操作Service实现
* @createDate 2026-04-24 23:24:54
*/
@Service
public class InboundServiceImpl extends ServiceImpl<InboundMapper, Inbound>
    implements InboundService{

}





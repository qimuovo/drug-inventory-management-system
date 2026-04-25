declare namespace API {
  type BaseResponseBoolean_ = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponsePageDrugVO_ = {
    code?: number;
    data?: PageDrugVO_;
    message?: string;
  };

  type BaseResponsePageInboundReturnVO_ = {
    code?: number;
    data?: PageInboundReturnVO_;
    message?: string;
  };

  type BaseResponsePageInboundVO_ = {
    code?: number;
    data?: PageInboundVO_;
    message?: string;
  };

  type BaseResponsePageManufacturerVO_ = {
    code?: number;
    data?: PageManufacturerVO_;
    message?: string;
  };

  type BaseResponsePageOutboundReturnVO_ = {
    code?: number;
    data?: PageOutboundReturnVO_;
    message?: string;
  };

  type BaseResponsePageOutboundVO_ = {
    code?: number;
    data?: PageOutboundVO_;
    message?: string;
  };

  type BaseResponseUserLoginVO_ = {
    code?: number;
    data?: UserLoginVO;
    message?: string;
  };

  type BaseResponseUserVO_ = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type deleteDrugUsingDELETEParams = {
    /** id */
    id: number;
  };

  type deleteManufacturerUsingDELETEParams = {
    /** id */
    id: number;
  };

  type DrugAddRequest = {
    drugCode?: string;
    drugName?: string;
    manufacturerId?: number;
    specification?: string;
  };

  type DrugQueryRequest = {
    current?: number;
    drugCode?: string;
    drugName?: string;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
  };

  type DrugUpdateRequest = {
    drugCode?: string;
    drugName?: string;
    manufacturerId?: number;
    specification?: string;
  };

  type DrugVO = {
    createTime?: string;
    drugCode?: string;
    drugName?: string;
    id?: number;
    manufacturerId?: number;
    manufacturerName?: string;
    specification?: string;
    updateTime?: string;
  };

  type InboundAddItemRequest = {
    batchNo?: string;
    drugId?: number;
    price?: number;
    quantity?: number;
  };

  type InboundAddRequest = {
    inboundDate?: string;
    inboundNo?: string;
    itemList?: InboundAddItemRequest[];
    remark?: string;
  };

  type InboundItemVO = {
    amount?: number;
    batchNo?: string;
    drugCode?: string;
    drugId?: number;
    drugName?: string;
    id?: number;
    inboundId?: number;
    price?: number;
    quantity?: number;
  };

  type InboundQueryRequest = {
    current?: number;
    inboundNo?: string;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
  };

  type InboundReturnAddItemRequest = {
    inboundItemId?: number;
    reason?: string;
    returnPrice?: number;
    returnQuantity?: number;
  };

  type InboundReturnAddRequest = {
    itemList?: InboundReturnAddItemRequest[];
    reason?: string;
    returnDate?: string;
  };

  type InboundReturnQueryRequest = {
    current?: number;
    inboundItemId?: number;
    pageSize?: number;
    search?: string;
    sortField?: string;
    sortOrder?: string;
  };

  type InboundReturnVO = {
    batchNo?: string;
    createTime?: string;
    drugCode?: string;
    drugId?: number;
    drugName?: string;
    id?: number;
    inboundNo?: string;
    inboundItemId?: number;
    operatorId?: number;
    reason?: string;
    returnDate?: string;
    returnPrice?: number;
    returnQuantity?: number;
  };

  type InboundVO = {
    createTime?: string;
    id?: number;
    inboundDate?: string;
    inboundNo?: string;
    itemList?: InboundItemVO[];
    operatorId?: number;
    remark?: string;
    updateTime?: string;
  };

  type ManufacturerAddRequest = {
    address?: string;
    contactPerson?: string;
    manufacturerName?: string;
    phone?: string;
  };

  type ManufacturerQueryRequest = {
    current?: number;
    manufacturerName?: string;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
  };

  type ManufacturerUpdateRequest = {
    address?: string;
    contactPerson?: string;
    manufacturerName?: string;
    phone?: string;
  };

  type ManufacturerVO = {
    address?: string;
    contactPerson?: string;
    createTime?: string;
    id?: number;
    manufacturerName?: string;
    phone?: string;
    updateTime?: string;
  };

  type OutboundAddItemRequest = {
    batchNo?: string;
    drugId?: number;
    price?: number;
    quantity?: number;
  };

  type OutboundAddRequest = {
    itemList?: OutboundAddItemRequest[];
    outboundDate?: string;
    outboundNo?: string;
    remark?: string;
  };

  type OutboundItemVO = {
    amount?: number;
    batchNo?: string;
    drugCode?: string;
    drugId?: number;
    drugName?: string;
    id?: number;
    outboundId?: number;
    price?: number;
    quantity?: number;
  };

  type OutboundQueryRequest = {
    current?: number;
    outboundNo?: string;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
  };

  type OutboundReturnAddItemRequest = {
    outboundItemId?: number;
    reason?: string;
    returnPrice?: number;
    returnQuantity?: number;
  };

  type OutboundReturnAddRequest = {
    itemList?: OutboundReturnAddItemRequest[];
    reason?: string;
    returnDate?: string;
  };

  type OutboundReturnQueryRequest = {
    current?: number;
    outboundItemId?: number;
    pageSize?: number;
    search?: string;
    sortField?: string;
    sortOrder?: string;
  };

  type OutboundReturnVO = {
    batchNo?: string;
    createTime?: string;
    drugCode?: string;
    drugId?: number;
    drugName?: string;
    id?: number;
    operatorId?: number;
    outboundNo?: string;
    outboundItemId?: number;
    reason?: string;
    returnDate?: string;
    returnPrice?: number;
    returnQuantity?: number;
  };

  type OutboundVO = {
    createTime?: string;
    id?: number;
    itemList?: OutboundItemVO[];
    operatorId?: number;
    outboundDate?: string;
    outboundNo?: string;
    remark?: string;
    updateTime?: string;
  };

  type PageDrugVO_ = {
    current?: number;
    pages?: number;
    records?: DrugVO[];
    size?: number;
    total?: number;
  };

  type PageInboundReturnVO_ = {
    current?: number;
    pages?: number;
    records?: InboundReturnVO[];
    size?: number;
    total?: number;
  };

  type PageInboundVO_ = {
    current?: number;
    pages?: number;
    records?: InboundVO[];
    size?: number;
    total?: number;
  };

  type PageManufacturerVO_ = {
    current?: number;
    pages?: number;
    records?: ManufacturerVO[];
    size?: number;
    total?: number;
  };

  type PageOutboundReturnVO_ = {
    current?: number;
    pages?: number;
    records?: OutboundReturnVO[];
    size?: number;
    total?: number;
  };

  type PageOutboundVO_ = {
    current?: number;
    pages?: number;
    records?: OutboundVO[];
    size?: number;
    total?: number;
  };

  type updateDrugUsingPUTParams = {
    /** id */
    id: number;
  };

  type updateManufacturerUsingPUTParams = {
    /** id */
    id: number;
  };

  type UserLoginRequest = {
    account?: string;
    password?: string;
  };

  type UserLoginVO = {
    token?: string;
    userInfo?: UserVO;
  };

  type UserVO = {
    avatar?: string;
    createTime?: string;
    id?: number;
    phone?: string;
    userAccount?: string;
    userName?: string;
  };
}

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

  type BaseResponsePageManufacturer_ = {
    code?: number;
    data?: PageManufacturer_;
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

  type Manufacturer = {
    address?: string;
    contactPerson?: string;
    createTime?: string;
    id?: number;
    isDeleted?: number;
    manufacturerName?: string;
    phone?: string;
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

  type PageDrugVO_ = {
    current?: number;
    pages?: number;
    records?: DrugVO[];
    size?: number;
    total?: number;
  };

  type PageManufacturer_ = {
    current?: number;
    pages?: number;
    records?: Manufacturer[];
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

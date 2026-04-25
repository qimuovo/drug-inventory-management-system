// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 新增药品 POST /api/drug */
export async function addDrugUsingPost(
  body: API.DrugAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/drug", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 编辑药品 PUT /api/drug/${param0} */
export async function updateDrugUsingPut(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.updateDrugUsingPUTParams,
  body: API.DrugUpdateRequest,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(`/api/drug/${param0}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    params: { ...queryParams },
    data: body,
    ...(options || {}),
  });
}

/** 删除药品 DELETE /api/drug/${param0} */
export async function deleteDrugUsingDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteDrugUsingDELETEParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(`/api/drug/${param0}`, {
    method: "DELETE",
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 分页查询库存汇总 POST /api/drug/inventory/summary/page */
export async function listDrugInventorySummaryByPageUsingPost(
  body: API.DrugInventorySummaryQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageDrugInventorySummaryVO_>(
    "/api/drug/inventory/summary/page",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** 导出库存汇总 POST /api/drug/inventory/summary/export */
export async function exportDrugInventorySummaryUsingPost(
  body: API.DrugInventorySummaryQueryRequest,
  options?: { [key: string]: any }
) {
  return request<Blob>("/api/drug/inventory/summary/export", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    responseType: "blob",
    data: body,
    ...(options || {}),
  });
}

/** 分页获取药品列表 POST /api/drug/page */
export async function listDrugByPageUsingPost(
  body: API.DrugQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageDrugVO_>("/api/drug/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

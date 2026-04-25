// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 新增厂家 POST /api/manufacturer */
export async function addManufacturerUsingPost(
  body: API.ManufacturerAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/manufacturer", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 编辑厂家 PUT /api/manufacturer/${param0} */
export async function updateManufacturerUsingPut(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.updateManufacturerUsingPUTParams,
  body: API.ManufacturerUpdateRequest,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(`/api/manufacturer/${param0}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    params: { ...queryParams },
    data: body,
    ...(options || {}),
  });
}

/** 删除厂家 DELETE /api/manufacturer/${param0} */
export async function deleteManufacturerUsingDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteManufacturerUsingDELETEParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(`/api/manufacturer/${param0}`, {
    method: "DELETE",
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 分页获取厂家列表 POST /api/manufacturer/page */
export async function listManufacturerByPageUsingPost(
  body: API.ManufacturerQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageManufacturerVO_>(
    "/api/manufacturer/page",
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

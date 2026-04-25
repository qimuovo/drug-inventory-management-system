// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 新增出库 POST /api/outbound */
export async function addOutboundUsingPost(
  body: API.OutboundAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/outbound", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 分页查询出库记录 POST /api/outbound/page */
export async function listOutboundByPageUsingPost(
  body: API.OutboundQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageOutboundVO_>("/api/outbound/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

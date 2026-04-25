// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 新增入库 POST /api/inbound */
export async function addInboundUsingPost(
  body: API.InboundAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/inbound", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 分页查询入库记录 POST /api/inbound/page */
export async function listInboundByPageUsingPost(
  body: API.InboundQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageInboundVO_>("/api/inbound/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 新增出库退库 POST /api/outbound-return */
export async function addOutboundReturnUsingPost(
  body: API.OutboundReturnAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/outbound-return", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 分页查询出库退库记录 POST /api/outbound-return/page */
export async function listOutboundReturnByPageUsingPost(
  body: API.OutboundReturnQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageOutboundReturnVO_>(
    "/api/outbound-return/page",
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

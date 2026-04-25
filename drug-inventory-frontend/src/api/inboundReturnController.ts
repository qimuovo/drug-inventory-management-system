// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 新增入库退货 POST /api/inbound-return */
export async function addInboundReturnUsingPost(
  body: API.InboundReturnAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/inbound-return", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 分页查询入库退货记录 POST /api/inbound-return/page */
export async function listInboundReturnByPageUsingPost(
  body: API.InboundReturnQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageInboundReturnVO_>(
    "/api/inbound-return/page",
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

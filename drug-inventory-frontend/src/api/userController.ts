// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 获取当前登录用户 GET /api/user/get/login */
export async function getLoginUserUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseUserVO_>("/api/user/get/login", {
    method: "GET",
    ...(options || {}),
  });
}

/** 用户登录 POST /api/user/login */
export async function loginUsingPost(
  body: API.UserLoginRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUserLoginVO_>("/api/user/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

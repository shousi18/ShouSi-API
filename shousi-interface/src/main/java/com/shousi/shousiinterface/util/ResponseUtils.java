package com.shousi.shousiinterface.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shousi.excepiton.ApiException;
import com.shousi.model.response.BaseResultResponse;

import static com.shousi.shousiinterface.util.HttpUtils.get;

import java.util.Map;

public class ResponseUtils {
    public static Map<String, Object> responseToMap(String response) {
        return new Gson().fromJson(response, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static <T> BaseResultResponse baseResultResponse(String baseUrl, T params) {
        String response = null;
        try {
            response = get(baseUrl, params);
            Map<String, Object> fromResponse = responseToMap(response);
            boolean success = (boolean) fromResponse.get("success");
            BaseResultResponse baseResultResponse = new BaseResultResponse();
            if (!success) {
                baseResultResponse.setData(fromResponse);
                return baseResultResponse;
            }
            fromResponse.remove("success");
            baseResultResponse.setData(fromResponse);
            return baseResultResponse;
        } catch (ApiException e) {
            throw new RuntimeException("构建url异常");
        }
    }
}

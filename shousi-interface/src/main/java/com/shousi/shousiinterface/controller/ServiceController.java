package com.shousi.shousiinterface.controller;

import cn.hutool.json.JSONUtil;
import com.shousi.excepiton.ApiException;
import com.shousi.model.params.*;
import com.shousi.model.response.BaseResultResponse;
import com.shousi.model.response.NameResponse;
import com.shousi.model.response.RandomWallpaperResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.shousi.shousiinterface.util.HttpUtils.buildUrl;
import static com.shousi.shousiinterface.util.HttpUtils.get;
import static com.shousi.shousiinterface.util.ResponseUtils.baseResultResponse;
import static com.shousi.shousiinterface.util.ResponseUtils.responseToMap;

@RestController
public class ServiceController {
    @GetMapping("/name")
    public NameResponse getName(NameParams nameParams) {
        return JSONUtil.toBean(JSONUtil.toJsonStr(nameParams), NameResponse.class);
    }

    @GetMapping("/loveTalk")
    public String randomLoveTalk() {
        return get("https://api.vvhan.com/api/love");
    }

    @GetMapping("/poisonousChickenSoup")
    public String getPoisonousChickenSoup() {
        return get("https://api.btstu.cn/yan/api.php?charset=utf-8&encode=json");
    }

    @GetMapping("/randomWallpaper")
    public RandomWallpaperResponse randomWallpaper(RandomWallpaperParams randomWallpaperParams) throws ApiException {
        String baseUrl = "https://api.btstu.cn/sjbz/api.php";
        String url = buildUrl(baseUrl, randomWallpaperParams);
        if (StringUtils.isAllBlank(randomWallpaperParams.getLx(), randomWallpaperParams.getMethod())) {
            url = url + "?format=json";
        } else {
            url = url + "&format=json";
        }
        return JSONUtil.toBean(get(url), RandomWallpaperResponse.class);
    }
    @PostMapping("/randomWallpaper")
    public RandomWallpaperResponse postAndomWallpaper(@RequestBody RandomWallpaperParams randomWallpaperParams) throws ApiException {
        String baseUrl = "https://api.btstu.cn/sjbz/api.php";
        String url = buildUrl(baseUrl, randomWallpaperParams);
        if (StringUtils.isAllBlank(randomWallpaperParams.getLx(), randomWallpaperParams.getMethod())) {
            url = url + "?format=json";
        } else {
            url = url + "&format=json";
        }
        return JSONUtil.toBean(get(url), RandomWallpaperResponse.class);
    }

    @GetMapping("/horoscope")
    public BaseResultResponse getHoroscope(HoroscopeParams horoscopeParams) throws ApiException {
        String response = get("https://api.vvhan.com/api/horoscope", horoscopeParams);
        Map<String, Object> fromResponse = responseToMap(response);
        boolean success = (boolean) fromResponse.get("success");
        if (!success) {
            BaseResultResponse baseResultResponse = new BaseResultResponse();
            baseResultResponse.setData(fromResponse);
            return baseResultResponse;
        }
        return JSONUtil.toBean(response, BaseResultResponse.class);
    }

    @GetMapping("/ipInfo")
    public BaseResultResponse getIpInfo(IpInfoParams ipInfoParams) {
        return baseResultResponse("https://api.vvhan.com/api/getIpInfo", ipInfoParams);
    }

    @GetMapping("/weather")
    public BaseResultResponse getWeatherInfo(WeatherParams weatherParams) {
        return baseResultResponse("https://api.vvhan.com/api/weather", weatherParams);
    }
}

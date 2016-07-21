package com.rednovo.libs.net.okhttp.cookie;

//import com.squareup.okhttp.Cookie;
//import com.squareup.okhttp.CookieJar;

public final class SimpleCookieJar/* implements CookieJar*/ {/*
    private final List<Cookie> allCookies = new ArrayList<>();

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies)
    {
        allCookies.addAll(cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url)
    {
        List<Cookie> result = new ArrayList<>();
        for (Cookie cookie : allCookies)
        {
            if (cookie.matches(url))
            {
                result.add(cookie);
            }
        }
        return result;
    }*/
}

package com.auv.model;

import com.alibaba.fastjson.JSON;

import java.util.concurrent.ConcurrentHashMap;

public class Result extends ConcurrentHashMap<String, Object> {

    /**
     *          200（成功）  服务器已成功处理了请求。通常，这表示服务器提供了请求的网页。
     * <p>
     *         201（已创建）  请求成功且服务器已创建了新的资源。
     * <p>
     *         202（已接受）  服务器已接受了请求，但尚未对其进行处理。
     * <p>
     *         203（非授权信息）  服务器已成功处理了请求，但返回了可能来自另一来源的信息。
     * <p>
     *         204（无内容）  服务器成功处理了请求，但未返回任何内容。
     * <p>
     *         205（重置内容） 服务器成功处理了请求，但未返回任何内容。与 204 响应不同，此响应要求请求者重置文档视图（例如清除表单内容以输入新内容）。
     * <p>
     *         206（部分内容）  服务器成功处理了部分 GET 请求。
     * <p>
     *         300-3007表示的意思是：要完成请求，您需要进一步进行操作。通常，这些状态代码是永远重定向的。
     * <p>
     *         300（多种选择）  服务器根据请求可执行多种操作。服务器可根据请求者 来选择一项操作，或提供操作列表供其选择。
     * <p>
     *         301（永久移动）  请求的网页已被永久移动到新位置。服务器返回此响应时，会自动将请求者转到新位置。您应使用此代码通知搜索引擎蜘蛛网页或网站已被永久移动到新位置。
     * <p>
     *         302（临时移动） 服务器目前正从不同位置的网页响应请求，但请求者应继续使用原有位置来进行以后的请求。会自动将请求者转到不同的位置。但由于搜索引擎会继续抓取原有位置并将其编入索引，因此您不应使用此代码来告诉搜索引擎页面或网站已被移动。
     * <p>
     *         303（查看其他位置） 当请求者应对不同的位置进行单独的 GET 请求以检索响应时，服务器会返回此代码。对于除 HEAD 请求之外的所有请求，服务器会自动转到其他位置。
     * <p>
     *         304（未修改） 自从上次请求后，请求的网页未被修改过。服务器返回此响应时，不会返回网页内容。
     * <p>
     *         如果网页自请求者上次请求后再也没有更改过，您应当将服务器配置为返回此响应。由于服务器可以告诉 搜索引擎自从上次抓取后网页没有更改过，因此可节省带宽和开销。
     * <p>
     *         305（使用代理） 请求者只能使用代理访问请求的网页。如果服务器返回此响应，那么，服务器还会指明请求者应当使用的代理。
     * <p>
     *         307（临时重定向）  服务器目前正从不同位置的网页响应请求，但请求者应继续使用原有位置来进行以后的请求。会自动将请求者转到不同的位置。但由于搜索引擎会继续抓取原有位置并将其编入索引，因此您不应使用此代码来告诉搜索引擎某个页面或网站已被移动。
     * <p>
     *         4XXHTTP状态码表示请求可能出错，会妨碍服务器的处理。
     * <p>
     *         400（错误请求） 服务器不理解请求的语法。
     * <p>
     *         401（身份验证错误） 此页要求授权。您可能不希望将此网页纳入索引。
     * <p>
     *         403（禁止） 服务器拒绝请求。
     * <p>
     *         404（未找到） 服务器找不到请求的网页。例如，对于服务器上不存在的网页经常会返回此代码。
     * <p>
     *         例如：http://www.0631abc.com/20100aaaa，就会进入404错误页面
     * <p>
     *         405（方法禁用） 禁用请求中指定的方法。
     * <p>
     *         406（不接受） 无法使用请求的内容特性响应请求的网页。
     * <p>
     *         407（需要代理授权） 此状态码与 401 类似，但指定请求者必须授权使用代理。如果服务器返回此响应，还表示请求者应当使用代理。
     * <p>
     *         408（请求超时） 服务器等候请求时发生超时。
     * <p>
     *         409（冲突） 服务器在完成请求时发生冲突。服务器必须在响应中包含有关冲突的信息。服务器在响应与前一个请求相冲突的 PUT 请求时可能会返回此代码，以及两个请求的差异列表。
     * <p>
     *         410（已删除） 请求的资源永久删除后，服务器返回此响应。该代码与 404（未找到）代码相似，但在资源以前存在而现在不存在的情况下，有时会用来替代 404 代码。如果资源已永久删除，您应当使用 301 指定资源的新位置。
     * <p>
     *         411（需要有效长度） 服务器不接受不含有效内容长度标头字段的请求。
     * <p>
     *         412（未满足前提条件） 服务器未满足请求者在请求中设置的其中一个前提条件。
     * <p>
     *         413（请求实体过大） 服务器无法处理请求，因为请求实体过大，超出服务器的处理能力。
     * <p>
     *         414（请求的 URI 过长） 请求的 URI（通常为网址）过长，服务器无法处理。
     * <p>
     *         415（不支持的媒体类型） 请求的格式不受请求页面的支持。
     * <p>
     *         416（请求范围不符合要求） 如果页面无法提供请求的范围，则服务器会返回此状态码。
     * <p>
     *         417（未满足期望值） 服务器未满足"期望"请求标头字段的要求。
     * <p>
     *           500至505表示的意思是：服务器在尝试处理请求时发生内部错误。这些错误可能是服务器本身的错误，而不是请求出错。
     * <p>
     *            500（服务器内部错误）  服务器遇到错误，无法完成请求。
     * <p>
     *           501（尚未实施） 服务器不具备完成请求的功能。例如，当服务器无法识别请求方法时，服务器可能会返回此代码。
     * <p>
     *           502（错误网关） 服务器作为网关或代理，从上游服务器收到了无效的响应。
     * <p>
     *           503（服务不可用） 目前无法使用服务器（由于超载或进行停机维护）。通常，这只是一种暂时的状态。
     * <p>
     *           504（网关超时）  服务器作为网关或代理，未及时从上游服务器接收请求。
     * <p>
     *           505（HTTP 版本不受支持） 服务器不支持请求中所使用的 HTTP 协议版本
     */

    private int code;
    private String msg;

    public static int SUCCESS_MEITUAN = 0; //  0（成功）美团返回的成功值
    public static int SUCCESS = 200; //  200（成功）  服务器已成功处理了请求。通常，这表示服务器提供了请求的网页。
    public static int FAIL = 400; //  400（错误请求） 服务器不理解请求的语法。

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return this.getCode() == SUCCESS;
    }

    public static Result success() {
        Result result = new Result();
        result.setCode( SUCCESS );
        result.setMsg( "success" );
        return result;
    }

    public static Result success(String msg) {
        Result result = new Result();
        result.setCode( SUCCESS );
        result.setMsg( msg );
        return result;
    }

    public static Result error() {
        Result result = new Result();
        result.setCode( FAIL );
        result.setMsg( "error" );
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.setCode( FAIL );
        result.setMsg( msg );
        return result;
    }

    public static Result error(int code) {
        Result result = new Result();
        result.setCode( code );
        result.setMsg( "error" );
        return result;
    }

    public static Result error(int code, String msg) {
        Result result = new Result();
        result.setCode( code );
        result.setMsg( msg );
        return result;
    }

    @Override
    public Result put(String key, Object value) {
        super.put( key, value );
        return this;
    }

    public String toJsonString() {
        return JSON.toJSONString( this );
    }
}
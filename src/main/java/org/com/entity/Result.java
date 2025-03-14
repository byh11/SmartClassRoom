package org.com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result<T> {
    private int code;       //响应吗 200-成功  100-失败
    private String message; //响应信息
    private T data;         //响应数据

    public static <E> Result<E> success(E data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <E> Result<E> success(String message, E data) {
        return new Result<>(200, message, data);
    }

    public static Result success(String message) {
        return new Result(200, message, null);
    }

    public static Result success() {
        return new Result(200, "操作成功", null);
    }

    public static Result error(String message) {
        return new Result<>(100, message, null);
    }

    public static Result error() {
        return new Result<>(100, "操作失败", null);
    }
}

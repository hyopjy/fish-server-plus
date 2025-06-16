package fish.plus.data.vo;

import lombok.Data;

@Data
public class Result <T>{
    private int code;

    private String message;

    private T data;


    public static <T> Result<T> ok(T data) {
        Result<T> t = new Result<>();
        t.setCode(200);
        t.setMessage("OK");
        t.setData(data);
        return t;
    }

    public static <T> Result<T> fail(int code, String message) {
        Result<T> t = new Result<>();
        t.setCode(code);
        t.setMessage(message);
        t.setData(null);
        return t;
    }
}

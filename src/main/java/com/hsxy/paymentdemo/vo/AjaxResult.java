package com.hsxy.paymentdemo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @name AjaxResult
 * @Description JSON统一结果返回:操作消息提醒
 * @author WU
 * @Date 2022/8/23 13:59
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class AjaxResult {
	private Integer code;
	private String message;
	//不用private Object data;∵可能带多个对象?
	private Map<String, Object> data = new HashMap<>();
	
	public AjaxResult(int code, String msg)
	{
		this.code = code;
		this.message = msg;
		/*this.data("code", code);
		this.data("message", msg);*/
	}
	public static AjaxResult ok(){
		/*AjaxResult r = new AjaxResult();
		r.setCode(0);
		r.setMessage("成功");
		return r;*/
		return new AjaxResult(200,"成功");
	}
	
	public static AjaxResult error(){
		/*AjaxResult r = new AjaxResult();
		r.setCode(-1);
		r.setMessage("失败");
		return r;*/
		return new AjaxResult(-1,"失败");
	}
	
	public static AjaxResult queryFailed(){

		return new AjaxResult(441,"查询失败");
	}
	
	/**
	 * @Description 方便链式调用
	 * @Param [key, value] {键,值}
	 * @return com.hsxy.paymentdemo.vo.AjaxResult {数据对象}
	 */
	public AjaxResult data(String key, Object value){
		//super.put(key, value);
		this.data.put(key, value);
		return this;
	}
}

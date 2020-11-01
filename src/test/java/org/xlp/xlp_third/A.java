package org.xlp.xlp_third;

import org.xlp.excel.annotation.ExcelField;
import org.xlp.json.annotation.FieldName;

public class A {
	@ExcelField(name="bean1")
	private int _a;

	@FieldName(name="E")
	@ExcelField(name="h啊")
	private int ds_a_Ac;
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("A [_a=").append(_a).append(", ds_a_Ac=").append(ds_a_Ac).append(", name=").append(name)
				.append("]");
		return builder.toString();
	}

	@FieldName
	private String name = "ui";

	public void set_a(int _a) {
		this._a = _a;
	}
	
	public int get_a() {
		return _a;
	}

	public int getDs_a_Ac() {
		return ds_a_Ac;
	}

	public void setDs_a_Ac(int ds_a_Ac) {
		this.ds_a_Ac = ds_a_Ac;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

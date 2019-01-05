package com.tohsoft.lib;

public class App {
	private String mpackage;
	private String name;
	private String icon;

	public App(String mpackage, String icon, String name) {
		this.mpackage = mpackage;
		this.name = name;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getMpackage() {
		return mpackage;
	}

	public void setMpackage(String mpackage) {
		this.mpackage = mpackage;
	}

	public static boolean isEqual(App a, App b) {
		boolean result = true;

		if (a.name.equals(b.name) && a.icon.equals(b.icon)
				&& a.mpackage.equals(b.mpackage)) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}
}
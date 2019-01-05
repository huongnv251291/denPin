package com.tohsoft.lib;

public class AppAdsObject {
    private String title;
    private String des;
    private String pkg;
    private byte[] iconSize64;
    private byte[] iconSize256;
    private byte[] iconSize512;
    private int tampInt1;
    private String tampStr1;
    private String tampStr2;
    private byte[] tampIcon1;
    public AppAdsObject(String title, String des, String pkg,
    		byte[] iconSize64, byte[] iconSize256, byte[] iconSize512,
    		int tampInt1, String tampStr1, String tampStr2, byte[] tampIcon1){
        this.title = title;
        this.des = des;
        this.pkg = pkg;
        this.iconSize64 = iconSize64;
        this.iconSize256 = iconSize256;
        this.iconSize512 = iconSize512;
        this.tampInt1 = tampInt1;
        this.tampStr1 = tampStr1;
        this.tampStr2 = tampStr2;
        this.tampIcon1 = tampIcon1;
    }
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	public byte[] getIconSize64() {
		return iconSize64;
	}
	public void setIconSize64(byte[] iconSize64) {
		this.iconSize64 = iconSize64;
	}
	public byte[] getIconSize256() {
		return iconSize256;
	}
	public void setIconSize256(byte[] iconSize256) {
		this.iconSize256 = iconSize256;
	}
	public byte[] getIconSize512() {
		return iconSize512;
	}
	public void setIconSize512(byte[] iconSize512) {
		this.iconSize512 = iconSize512;
	}
	public int getTampInt1() {
		return tampInt1;
	}
	public void setTampInt1(int tampInt1) {
		this.tampInt1 = tampInt1;
	}
	public String getTampStr1() {
		return tampStr1;
	}
	public void setTampStr1(String tampStr1) {
		this.tampStr1 = tampStr1;
	}
	public String getTampStr2() {
		return tampStr2;
	}
	public void setTampStr2(String tampStr2) {
		this.tampStr2 = tampStr2;
	}
	public byte[] getTampIcon1() {
		return tampIcon1;
	}
	public void setTampIcon1(byte[] tampIcon1) {
		this.tampIcon1 = tampIcon1;
	}
}

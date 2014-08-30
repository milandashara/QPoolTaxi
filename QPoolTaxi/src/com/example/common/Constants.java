package com.example.common;

public interface Constants {

	public static final String URL_BASE = "https://apex.oracle.com/pls/apex/viczsaurav/qpool/";
	public static final String URL_REGISTER = URL_BASE + "register/";
	public static final String URL_LOGIN = URL_BASE + "signin/";
	public static final String URL_TAXISTAND=URL_BASE+"gettaxistand/";
	public static final String URL_FINDMATCH=URL_BASE+"findmatch/";
	
	
	public static final int USER_SUCCESS=1;
	public static final int USER_ALREADY_EXIST=0;
}


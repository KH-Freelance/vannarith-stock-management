package com.hfsolution.app.dto;

import java.io.Serializable;

import com.hfsolution.app.util.AppTools;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseCache implements Serializable {
  
  private static final long serialVersionUID = -6314417395124554774L;

  String hasKeyName;
  String id;
  long timeToLiveAsSecond;
  String cachedDateTime = AppTools.getCurrentDateWithFormatString("dd-MMM-yyyy hh:mmSSS");// appGetCurrentDateWithFormat("dd-MM-yyyy HH:mm:ss");
}


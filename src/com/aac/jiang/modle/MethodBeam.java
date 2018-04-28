package com.aac.jiang.modle;
public class MethodBeam {
    private String methodName;
    private  String beanSName;
    private  int  rxType;
    private  String  uriName;
     private  String keyName;
     private  int converterType=0;
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getBeanSName() {
        return beanSName;
    }

    public void setBeanSName(String beanSName) {
        this.beanSName = beanSName;
    }

    public int getRxType() {
        return rxType;
    }

    public void setRxType(int rxType) {
        this.rxType = rxType;
    }

    public String getUriName() {
        return uriName;
    }

    public void setUriName(String uriName) {
        this.uriName = uriName;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public int getConverterType() {
        return converterType;
    }

    public void setConverterType(int converterType) {
        this.converterType = converterType;
    }
}

# MOOZISMS-ANDROID_SDK
# Android sdk for send sms with moozisms platform

>Before using this sdk ensure you have your API keys and acces Token else you can get it at moozisms.com/dashboard

## Add Dependence to your project

### Add the code below in your root build.gradle file
```gradle   
    maven{
       url  "https://opensi.bintray.com/MOOZISMS"
    }
 ```
	
### Add the dependence in your module app build.gradle file
```gradle 
    compile 'com.craftsman.moozisms:sms-sender:1.0.2'
 ```

## Init SDK
 ```java
MOOZISMS moozisms = new MOOZISMS(/*Android Context*/,/*Account SID*/,/*Your Auth Token*/);
```

## SEND YOUR SMS
 ```java
moozisms.sendSms(/*Receiver phone Number*/,/*SENDER ALPHANUMERIC ID*/, /*Message Content*/
		new MOOZISMS.Callback() {
            @Override
            public void onFinish(boolean isSucces) {
             //checkout the success
            }
        });
 ```

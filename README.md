# android-multiple-images-selector

one easy-to-use selector to select images in Android application

# How to use this library in your application

## 1. include this in gradle
## 2. Initialize Fresco in your application
Permission & Application:

	<uses-permission android:name="android.permission.INTERNET" />
    <application
    	 ...
        android:name=".DemoApplication"
        ...
        
        <activity
            android:name="com.zfdang.multiple_images_selector.ImagesSelectorActivity"
            android:configChanges="orientation|screenSize"/>
        
	</application>

Intialization:

	public class DemoApplication extends Application
	{
    	@Override
    	public void onCreate() {
        super.onCreate();

        // the following line is important
        Fresco.initialize(getApplicationContext());
    	}
	}
 


## 3. Launch images selector by:

	Intent intent = new Intent(DemoActivity.this, ImagesSelectorActivity.class);
	startActivityForResult(intent, REQUEST_CODE);


## 4. get results by
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                // DO Things here
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


## 5. Customizations:

# Copyright

# Android Multiple Images Selector

Easy-to-use library to select images in Android application

Features:

* select images by folders
* support to set max number of images to be selected
* allow filter images with tiny size (configurable)
* fast scroll of image list
* support to capture images from camera
* compitable with android 6.0
* support Chinese / English

NOTE: this library depends on Fresco / rxjava. 

# How does it work (GIF)
![Demo](demo.gif)   ![Demo](capture.gif)

Download demo apk here: [demo](multiple-images-selector-demo.apk)

# How to use this library in your application

## 1. add "multiple-images-selector" dependency

multiple-images-selector is now available in jcenter.

Add "multiple-images-selector" as dependency to your app:

    compile 'com.zfdang.multiple-images-selector:multiple-images-selector:1.1.5'


## 2. Initialize Fresco & Add selector activity in your app's manifest
Permission & Application & Activity:

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <!--the following two permissions are required if you want to take photo in selector-->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    
    <application
    	 ...
        android:name=".DemoApplication"
        ...
        
        <activity
            android:name="com.zfdang.multiple_images_selector.ImagesSelectorActivity"
            android:configChanges="orientation|screenSize"/>
        
	</application>

Application intialization:

	public class DemoApplication extends Application
	{
    	@Override
    	public void onCreate() {
        super.onCreate();

        // the following line is important
        Fresco.initialize(getApplicationContext());
    	}
	}
 


## 3. Launch images selector:

	// class variables
    private static final int REQUEST_CODE = 123;
    private ArrayList<String> mResults = new ArrayList<>();

    // start multiple photos selector
    Intent intent = new Intent(DemoActivity.this, ImagesSelectorActivity.class);
    // max number of images to be selected
    intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 5);
    // min size of image which will be shown; to filter tiny images (mainly icons)
    intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
    // show camera or not
    intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);
    // pass current selected images as the initial value
    intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, mResults);
    // start the selector
    startActivityForResult(intent, REQUEST_CODE);


## 4. get results by
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // get selected images from selector
        if(requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                mResults = data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS);
                assert mResults != null;

                // show results in textview
                StringBuffer sb = new StringBuffer();
                sb.append(String.format("Totally %d images selected:", mResults.size())).append("\n");
                for(String result : mResults) {
                    sb.append(result).append("\n");
                }
                tvResults.setText(sb.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



## 5. Q&A:

Issue 1:	
	
	android.view.InflateException: Binary XML file line #7: Binary XML file line #7: Error inflating class com.facebook.drawee.view.SimpleDraweeView

Solution:	make sure Fresco is initialized in app properly.

	android:name=".DemoApplication"
	...
	Fresco.initialize(getApplicationContext());

Issue 2:
	
	App crash while loading large amount of images
	
Solution: disable hardware acceleration for activity

	<activity
        android:name="com.zfdang.multiple_images_selector.ImagesSelectorActivity"
        android:hardwareAccelerated="false"
        android:configChanges="orientation|screenSize"/>

	
# License

Multiple-Images-Selector is [MIT-licensed](LICENSE).

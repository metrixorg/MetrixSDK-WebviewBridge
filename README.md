# اضافه‌کردن کتابخانه متریکس به پروژه

۱. مخزن متریکس را در فایل `build.gradle` مربوط به پروژه خود در قسمت `allprojects` اضافه کنید:

```groovy
allprojects {
    repositories {
        // ...
        
        maven { url "https://dl.bintray.com/metrixorg/maven" }
    }
}
```

۲. وابستگی کتابخانه متریکس را در فایل `build.gradle` مربوط به اپلیکیشن خود اضافه کنید:

```groovy
dependencies {
    // ...

    implementation 'ir.metrix:webview:1.0.0-beta2'
}
```


# راه‌اندازی کتابخانه

۱. شناسه اپلیکیشن خود را به صورت `meta-data` در فایل `AndroidManifest.xml` اپلیکیشن خود قرار دهید:

```xml
<application
  <!-- ... -->

  <meta-data
      android:name="metrix_appId"
      android:value="YOUR_APP_ID" />

</application>
```

`APP_ID`: کلید اپلیکیشن شما که از پنل متریکس آن را دریافت می‌کنید.

۲. پس از دریافت اشاره‌گر به `Weview` خود: 

- متد `webView.getSettings().setJavaScriptEnabled(true)` را جهت فعال‌سازی `javascript` در `webview` فراخوانی کنید.
- متد `MetrixBridge.registerAndGetInstance(webview)` را جهت فعال‌سازی واسط متریکس میان کتابخانه و `webview` فراخوانی کنید.
- در صورت نیاز به تغییر `webview` می‌توانید متد `MetrixBridge.setWebView(newWebview)` را فراخوانی کنید. 
- جهت غیرفعال کردن واسط، متد `MetrixBridge.unregister()` را فراخوانی کنید.

با انجام این مراحل، کلاس `Activity` شما مشابه قطعه کد زیر خواهد بود:

```java
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        MetrixBridge.registerAndGetInstance(webview);
        try {
            webView.loadUrl("file:///android_asset/MetrixExample-WebView.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onDestroy() {
        MetrixBridge.unregister();

        super.onDestroy();
    }
}
```

به این ترتیب کتابخانه متریکس با موفقیت در اپلیکیشن شما فعال شده است و واسط `Javascript` متریکس به عنوان راه ارتباطی میان کتابخانه متریکس و صفحات بارگذاری شده شما در `webview` عمل خواهد کرد.

جهت ارتباط با کتابخانه متریکس و استفاده از امکانات آن مانند ارسال رویداد، در فایل `HTML` خود، فایل `Javascript` متریکس را که در پوشه `assets` قرار دارد به صورت زیر import کنید:

```html
<script type="text/javascript" src="metrix.js"></script>
```

به این ترتیب می‌توانید در فایل `HTML` خود متدهای `Metrix` را فراخوانی کنید.


<hr/>
<br/>
# امکانات و قابلیت‌ها
<br/>

## نشست (session)

 شما و بازه زمانی آنها را جمع‌آوری می‌کند و در اختیار شما می‌گذارد.

### شناسه نشست

کتابخانه متریکس برای هر نشست یک شناسه منحصر به فرد تولید می‌کند که می‌توانید این شناسه را دریافت نمایید.
برای دریافت این شناسه متد زیر را فراخوانی کنید.

```java
  Metrix.getSessionId()
```

### شماره نشست جاری

با استفاده از متد زیر می‌توانید از شماره نشست جاری کاربر در تمام مدت استفاده خود از اپلیکیشن شما اطلاع پیدا کنید:

```java
Metrix.getSessionNum();
```
<br/>
## رویداد (event)
هرگونه تعاملی که کاربر با اپلیکیشن شما دارد می‌تواند به عنوان یک **رویداد** در پنل و اپلیکیشن شما تعریف شود تا کتابخانه متریکس اطلاعات آماری مربوط به آن را در اختیار شما قرار دهد.

در کتابخانه متریکس دو نوع رویداد قابل تعریف است:

- **سفارشی (custom):** وابسته به منطق اپلیکیشن شما و تعاملی که کاربر با اپلیکیشن شما دارد می‌توانید رویدادهای سفارشی خود را در قالبی که در ادامه شرح داده خواهد شد بسازید و ارسال کنید.
- **درآمدی (revenue):** نوع خاصی از رویدادهای سفارشی قابل تعریف است که مربوط به میزان درآمد کسب شده در اپلیکیشن شما می‌باشد و دارای یک مقدار قابل اندازه‌گیری از جنس درآمد مالی است.

### ساختن یک رویداد سفارشی

برای ساخت یک رویداد سفارشی در ابتدا در پنل خود از قسمت مدیریت رویدادها، رویداد موردنظر خود را ثبت کنید و نامک (slug) آن را به عنوان نام رویداد در اپلیکیشن استفاده کنید.

وقوع رویداد به دو صورت می‌تواند ثبت شود:

۱. ثبت رویداد تنها با استفاده از نامک آن که در پنل معرفی شده است:

```java
Metrix.newEvent("my_event_slug");
```

۲. ثبت رویداد به همراه تعداد دلخواه attribute مربوط به آن. به عنوان مثال فرض کنید در یک برنامه خرید آنلاین می‌خواهید یک رویداد سفارشی بسازید:

```javascript
var attributes = {};
attributes['first_name'] = 'Ali';
attributes['last_name'] = 'Bagheri';
attributes['manufacturer'] = 'Nike';
attributes['product_name'] = 'shirt';
attributes['type'] = 'sport';
attributes['size'] = 'large';

Metrix.newEvent('purchase_event_slug', attributes);
```

ورودی‌های متد **newEvent** در این حالت، بدین شرح هستند:

- **ورودی اول:** نامک رویداد مورد نظر شما که در پنل متریکس معرفی شده است.
- **ورودی دوم:** یک `Map<String, String>` که ویژگی‌های یک رویداد را مشخص می‌کند.

#### مشخص کردن Attribute‌های پیش‌فرض همه‌ی رویدادها

با استفاده از این تابع می‌توانید به تعداد دلخواه `Attribute` به همه‌ی رویدادهای خود اضافه کنید:

```javascript
var attributes = {};
attributes['manufacturer'] = 'Nike';

Metrix.addUserAttributes(attributes);
```

**توجه:** هر رویداد می‌تواند حداکثر ۵۰ attribute داشته باشد که طول key و value آن حداکثر ۵۱۲ بایت می‌باشد.


### ساختن رویداد درآمدی

با استفاده از این تابع می‌توانید یک رویداد درآمدی بسازید. برای این کار در ابتدا در پنل خود از قسمت مدیریت رویدادها، رویداد موردنظر خود را ثبت کنید و نامک (slug) آن را به عنوان نام رویداد در اپلیکیشن استفاده کنید.

```javascript
Metrix.newRevenue('my_event_slug', 12000, 0, '2');
```
ورودی‌های متد **newRevenue** بدین شرح هستند:

- **ورودی اول:** نامک رویداد مورد نظر شما که در پنل متریکس معرفی شده است.
- **ورودی دوم:** یک مقدار عددی است که همان میزان درآمد است.
- **ورودی سوم:** واحد پول مورد استفاده را تعیین می‌کند و می‌تواند سه مقدار **0 (ریال)**  (پیش‌فرض) یا **1 (دلار)** و یا **3 (یورو)** را داشته باشد.
- **ورودی چهارم:** این ورودی دلخواه است و شناسه سفارش را تعیین می‌کند.


<br/>
## دریافت شناسه دستگاه‌های متریکس

برای هر دستگاهی که اپلیکیشن شما را نصب کند، متریکس یک شناسه منحصر به فرد تولید می‌کند که شما می‌توانید این شناسه را به محض شناسایی دریافت نمایید.
برای دسترسی به این شناسه از متد استفاده کنید:

```javascript
Metrix.setUserIdListener(metrixUserId => {
  //TODO
});
```

<br/>
## امضاء

شما می‌توانید با فعال‌سازی قابلیت sdk signature در پنل خود و تعیین app secret های موجود، امنیت ارتباط و انتقال اطلاعات را افزایش داده و از سلامت آمار اپلیکیشن خود اطمینان بیشتری حاصل کنید.

برای فعال‌سازی از متد زیر استفاده کنید:

```javascript
Metrix.setAppSecret(secretId, info1, info2, info3, info4);
```

<br/>
## شمارش پاک کردن اپلیکیشن

متریکس برای شمارش پاک شدن اپلیکشن شما از ارسال پوش استفاده می‌کند.

**شما باید برای استفاده از این امکان از Firebase Cloud Messaging (FCM) استفاده نمایید و پس از دریافت توکن پوش، آن را از طریق متد زیر به متریکس دهید:

```javascript
Metrix.setPushToken(token);
```

<br/>
## دریافت اطلاعات کمپین

با استفاده از متد زیر، می‌توانید اطلاعات کمپین تبلیغاتی که در ترکر خود در پنل قرار داده‌اید را دریافت کنید.

```javascript
Metrix.setUserAttributionListener(attributionModel => {
  //TODO
});
```

مدل `attributionModel` اطلاعات زیر را در اختیار شما قرار می دهد.

`attributionModel.acquisitionAd` : نام تبلیغ

`attributionModel.acquisitionAdSet`: گروه تبلیغاتی

`attributionModel.acquisitionCampaign`: کمپین تبلیغاتی

`attributionModel.acquisitionSource`: شبکه تبلیغاتی

`attributionModel.attributionStatus`: وضعیت کاربر در کمپین را  
مشخص می کند

مقدار `AttributionStatus` شامل یکی از موارد زیر است:

- `ATTRIBUTED` اتربیوت شده
- `NOT_ATTRIBUTED_YET` هنوز اتربیوت نشده
- `ATTRIBUTION_NOT_NEEDED` نیاز به اتربیوت ندارد
- `UNKNOWN` حالت ناشناخته

<br/>
## Deep Linking

اگر شما از ترکر هایی که دیپ‌لینک در آنها فعال است استفاده کنید، می‌توانید اطلاعات url دیپ‌لینک و محتوای آن را دریافت کنید. دستگاه بر اساس نصب بودن اپلیکیشن (سناریو استاندارد) یا نصب نبودن اپلیکیشن (سناریو deferred) واکنش نشان می‌دهد.
در صورت نصب بودن اپلیکیشن شما اطلاعات دیپ‌لینک به اپلیکیشن شما ارسال می‌شود.

پلتفرم اندروید به صورت اتوماتیک سناریو deferred را پشتیبانی نمی‌کند. در این صورت متریکس سناریو مخصوص به خود را دارد تا بتواند اطلاعات دیپ‌لینک را به اپلیکیشن ارسال کند.

### سناریو استاندارد

اگر کاربران شما اپلیکیشن شما را نصب داشته باشند و شما بخواهید بعد از کلیک بر روی لینک دیپ‌لینک صفحه خاصی از اپلیکیشن شما باز شود ابتدا باید یک scheme name یکتا انتخاب کنید.
سپس آن را باید به اکتیویتی که قصد دارید در صورت کلیک بر روی دیپ‌لینک اجرا شود نسبت دهید. برای این منظور به فایل `AndroinManifest.xml` رفته و بخش `intent-filter` را به اکتیویتی مورد نظر اضافه کنید همچنین scheme name مورد نظر خود را نیز قرار دهید.
مانند زیر:

```xml
<activity
    android:name=".MainActivity"
    android:configChanges="orientation|keyboardHidden"
    android:label="@string/app_name"
    android:screenOrientation="portrait">

    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="metrixEample" />
    </intent-filter>
</activity>
```

اطلاعات دیپ‌لینک در اکتیویتی که آن را تعریف کردید توسط یک آبجکت `Intent` درمتد های `onCreate`و `newIntent` قابل دسترس است.

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Intent intent = getIntent();
    Uri data = intent.getData();
}
```

```java
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    Uri data = intent.getData();
}
```

### سناریو deferred

این سناریو زمانی رخ می‌هد که کاربر روی دیپ‌لینک کلیک می‌کند ولی اپلیکیشن شما را در زمانی که کلیک کرده روی دستگاه خود نصب نکرده است. وقتی کاربر کلیک کرد به گوگل پلی استور هدایت می‌شود تا اپلیکیشن شما را نصب کند. وقتی اپلیکیشن شما را نصب کرد و برای اولین بار آن را باز کرد اطلاعات دیپ‌لینک به اپلیکیشن داده می‌شود.

اگر شما قصد دارید که سناریو deferred را کنترل کنید می‌توانید از کالبک زیر استفاده نمایید:

```javascript
Metrix.shouldLaunchDeeplink = true;
Metrix.setDeeplinkResponseListener(deeplink => {
  //TODO
});
```

بعد از این که متریکس اطلاعات دیپ‌لینک را از سرور خود دریافت کرد محتوای آن را به کالبک بالا پاس می‌دهد. اگر مقدار `shouldLaunchDeeplink` برابر `true` باشد متریکس به صورت اتوماتیک سناریو استاندارد را اجرا می‌کند و در غیر این صورت، متریکس فقط اطلاعات را در این کالبک قرار می‌دهد تا شما بر اساس آن اکشن مورد نظر خود را انجام دهید.


## مشخص کردن Pre-installed Tracker

اگر بخواهید برای کاربرانی که نصب آنها organic بوده و از یک کلیک ناشی نمی‌شود ترکر داشته باشید، با استفاده از این تابع می‌توانید با یک `trackerToken` که از پنل دریافت می‌کنید، یک `tracker` پیش‌فرض برای اپلیکیشن خود قرار دهید:

```javascript
Metrix.setDefaultTracker(trackerToken);
```

## تفکیک بر‌اساس استور های اپلیکیشن

اگر شما می‌خواهید اپلیکیشن خود را در استور های مختلف مانند کافه بازار، گوگل پلی و … منتشر کنید، با استفاده از متد زیر می‌توانید مشاهده کنید که کاربر از کدام استور ( مثلا کافه بازار، گوگل پلی، مایکت، اول مارکت و وبسایت ... ) اپلیکیشن را نصب کرده و منبع نصب های ارگانیک خود را شناسایی کنید.

```javascript
Metrix.setStore("store name");
```
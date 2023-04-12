[![](https://jitpack.io/v/Y-E-P/BrowserFiP.svg)](https://jitpack.io/#Y-E-P/BrowserFiP)
# BrowserFiP

An Android library that lets you extend your WebView with Search Bar like in Chrome Browser function "Find in page".

<img width="50%" src="sample_demo.gif">


# Setup

First of all, include the dependency in your app build.gradle:
Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    implementation 'com.github.Y-E-P:BrowserFiP:1.0.2'
}
```

## Add View

After successfully installing dependencies add FiPSearchView in your xml:

```xml
<com.yupo.browserfiplib.FiPSearchView
    android:id="@+id/search"
    android:layout_width="match_parent"
    android:layout_height="?attr/actionBarSize"
    app:fip_hint="Your hint"
    app:fip_next_icon="@drawable/ic_your_icon"
    app:fip_previous_icon="@drawable/ic_your_icon"
    app:fip_close_icon="@drawable/ic_your_icon"
    app:fip_divider_color="@color/your_color"
    app:fip_divider_visible="true"
    app:fip_counter_empty_color="@color/your_color"
    app:fip_counter_matched_color="@color/your_color"
    app:fip_hint_color="@color/your_hint_color"
    app:fip_text_color="@color/your_text_color"
/>
```
All attributes are optional.

## Usage

### To use lib with your WebView you should pass WebView ass parameter.

```kotlin
val fip : FiPSearchView = findViewById(R.id.fipSearch)
fip.setupSearchComponent(yorWebView)
```

### FiP has a builder method that allowed you to customize the appearance

```kotlin
fip?.setupView {
        hint = //your hint
        nextArrowIcon = //your next arrow icon
        previousArrowIcon = //your previous arrow icon
        closeArrowIcon = //your close arrow icon
        dividerVisibility = //your divider visibility state true or false
        dividerColor = //your divider color
        counterEmptyColor = //your couter color
        counterMatchedColor = //your counter matched color 
        textColor = // your text color
        hintColor = // your hint color
}
```
### Release resources after using FiPSearch(for example in onDestroy(Activity) or onDestroyView(Fragment))

```kotlin
fip.release()
```


Also there are ability to subscribe on actions, like clicking on button NEXT, PREVIOUS and CLOSE. For this add following line of code:

```kotlin
fip.onNavigationClicked = { event->
    when (event) {
        FiPSearchView.ClickEvent.NEXT -> {}
        FiPSearchView.ClickEvent.PREVIOUS -> {}
        FiPSearchView.ClickEvent.CLOSE -> {}
    }
}
```

### Also you can check sample app code.

## Contribute

Feel free to contribute, let's make library more better. 

## License

```
  Copyright (c) 2023 Yurii Poudanien
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
  http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 
```